---
nav-title: Svelte 5 Class State
title: Svelte 5 State in Classes Simplifies My Code
description: How Svelte 5 $state in class fields made Atom Clicker managers smaller and more maintainable.
keywords: svelte, svelte 5, state, classes, runes, typescript, atom clicker
date-created: 2026-02-05
date-modified: 2026-02-05
root: .layouts.ArticleLayout
routeOverride: /articles/svelte-5-class-state/index
---

Svelte 5 made a small change that had a big impact on my code: you can place [$state](https://svelte.dev/docs/svelte/$state) directly in class fields.
That single capability let me replace a pile of tiny store files with focused managers that read like plain [TypeScript](https://www.typescriptlang.org/).
This article walks through the refactor I did in [Atom Clicker](https://github.com/Ayfri/Atom-Clicker) and why it made the code easier to reason about.

This is for my project [Atom Clicker](https://github.com/Ayfri/Atom-Clicker), a Svelte 5 [incremental game](https://en.wikipedia.org/wiki/Incremental_game) where you build atoms, unlock realms, and automate upgrades. The state surface is large, so clean state management matters a lot.

If you are new to Svelte 5 runes, the two key docs are:

- [$state](https://svelte.dev/docs/svelte/$state)
- [.svelte.js and .svelte.ts files](https://svelte.dev/docs/svelte/svelte-js-files)
- [What are runes?](https://svelte.dev/docs/svelte/what-are-runes)

## The short version: class fields are now reactive

Svelte 5 does not proxy class instances. Instead, you declare reactive fields using `$state` inside the class.
The compiler turns those fields into getters and setters, so reads and writes stay ergonomic:

- You write `this.atoms += 1` instead of `store.update(...)`.
- You keep all state and methods in one class, which makes features easier to find.
- You can still use [$derived](https://svelte.dev/docs/svelte/$derived) and [$effect](https://svelte.dev/docs/svelte/$effect) in the same class for computed values and side effects.

## The commits that triggered the shift

These are the changes that introduced class-based state managers in Atom Clicker:

- [GameManager refactor](https://github.com/Ayfri/Atom-Clicker/commit/47445d087f2fb171be12c134ebe54a2c6ce00df8) consolidated scattered stores into one class.
- [AutoBuy, AutoUpgrade, Realm managers](https://github.com/Ayfri/Atom-Clicker/commit/cc1dcb6ba6185bb3d47cbe174c4b769e9e5ce82d) moved automation and UI state into classes.
- [Component updates](https://github.com/Ayfri/Atom-Clicker/commit/04204bbc55e57cf8e028d4071a3d1e0a0c889fb0) replaced `gameStore` usage with `gameManager`.
- [Currencies manager](https://github.com/Ayfri/Atom-Clicker/commit/f63326fc243dc6cb8cd4d35e24724b9159fa2284) simplified currency logic and iteration.

The common theme is that managers became small, cohesive modules with their own state and behavior.

## Before/after: stores vs class state

Before, my state lived in scattered stores with helper functions. The shape below is a simplified example of the old [Svelte store](https://svelte.dev/docs/svelte/svelte-store) pattern:

```ts
import { derived, writable } from 'svelte/store';

export const atoms = writable(0);
export const upgrades = writable<string[]>([]);
export const skillUpgrades = writable<string[]>([]);

export const currentUpgradesBought = derived(
	[upgrades, skillUpgrades],
	([$upgrades, $skillUpgrades]) => [...$upgrades, ...$skillUpgrades]
);

export function purchaseUpgrade(id: string, cost: number) {
	atoms.update((value) => value - cost);
	upgrades.update((list) => [...list, id]);
}
```

After the refactor, state and behavior live side by side in a class, and the class fields are reactive:

```ts
export class GameManager {
	upgrades = $state<string[]>([]);
	skillUpgrades = $state<string[]>([]);
	atoms = $state(0);

	currentUpgradesBought = $derived.by(() => {
		return [...this.upgrades, ...this.skillUpgrades];
	});

	purchaseUpgrade(id: string, cost: number) {
		this.atoms -= cost;
		this.upgrades = [...this.upgrades, id];
	}
}
```

The big win is that all related logic sits in one class, without the indirection of store helpers.

## Example: a GameManager with real state and real methods

Because `$state` can live in class fields, a manager feels like plain TypeScript instead of a Svelte store wrapper.
Here is a trimmed down version from [GameManager.svelte.ts](https://github.com/Ayfri/Atom-Clicker/blob/main/src/lib/helpers/GameManager.svelte.ts):

```ts
export class GameManager {
	upgrades = $state<string[]>([]);
	skillUpgrades = $state<string[]>([]);
	atoms = $state(0);
	buildings = $state<Partial<Record<BuildingType, Building>>>({});
	settings = $state<Settings>({
		automation: {
			buildings: [],
			upgrades: false
		}
	});

	currentUpgradesBought = $derived.by(() => {
		const allUpgradeIds = [...this.upgrades, ...this.skillUpgrades];
		return allUpgradeIds
			.filter(id => UPGRADES[id] || SKILL_UPGRADES[id])
			.map(id => UPGRADES[id] || SKILL_UPGRADES[id]);
	});

	purchaseUpgrade(id: string) {
		const upgrade = UPGRADES[id];
		const purchased = this.upgrades.includes(id);

		if (!purchased && this.spendCurrency(upgrade.cost)) {
			this.upgrades = [...this.upgrades, id];
			this.totalUpgradesPurchased += 1;
			return true;
		}
		return false;
	}
}
```

This is still just a TypeScript class, but the fields are reactive. Components can import a single instance and read or write values directly.
That is the main simplification: no more wrapping everything in a store shape.

## Before/after: automation manager

Before, automation behavior often lived in a store file that exported timers and update functions. With class state, the same logic becomes a compact service. You can see the full implementation in [autoBuy.svelte.ts](https://github.com/Ayfri/Atom-Clicker/blob/main/src/lib/stores/autoBuy.svelte.ts):

```ts
class AutoBuyManager {
	recentlyAutoPurchasedBuildings = $state(new Map<BuildingType, number>());
	private timers: Record<string, ReturnType<typeof setInterval>> = {};

	init() {
		$effect(() => {
			const intervals = this.autoBuyIntervals;

			Object.values(this.timers).forEach(clearInterval);
			this.timers = {};

			Object.entries(intervals).forEach(([buildingType, interval]) => {
				this.timers[buildingType] = setInterval(() => {
					gameManager.purchaseBuilding(buildingType as BuildingType, 1);
				}, interval);
			});

			return () => Object.values(this.timers).forEach(clearInterval);
		});
	}
}
```

This keeps the lifecycle, timers, and reactive fields together, which is much easier to maintain.

## Smaller managers stay tiny

State in classes is just as useful for tiny, focused modules. [RealmManager](https://github.com/Ayfri/Atom-Clicker/blob/main/src/lib/helpers/RealmManager.svelte.ts) and [AutoBuyManager](https://github.com/Ayfri/Atom-Clicker/blob/main/src/lib/stores/autoBuy.svelte.ts) both benefit from this.
They hold their own reactive fields, and expose normal methods:

```ts
class RealmManager {
	selectedRealmId = $state('atoms');

	get selectedRealm() {
		return this.realms.find((r) => r.id === this.selectedRealmId) || this.realms[0];
	}

	selectRealm(id: string) {
		if (this.realms.find((r) => r.id === id && r.isUnlocked())) {
			this.selectedRealmId = id;
		}
	}
}
```

```ts
class AutoBuyManager {
	recentlyAutoPurchasedBuildings = $state(new Map<BuildingType, number>());
	private timers: Record<string, ReturnType<typeof setInterval>> = {};

	init() {
		$effect(() => {
			const intervals = this.autoBuyIntervals;

			Object.values(this.timers).forEach(clearInterval);
			this.timers = {};

			Object.entries(intervals).forEach(([buildingType, interval]) => {
				this.timers[buildingType] = setInterval(() => {
					gameManager.purchaseBuilding(buildingType as BuildingType, 1);
				}, interval);
			});

			return () => Object.values(this.timers).forEach(clearInterval);
		});
	}
}
```

The code reads like standard OOP, but the UI stays reactive because `$state` and `$effect` are doing the Svelte work behind the scenes.

## A currency manager that acts like a real service

A manager class also makes it easy to centralize a data structure with clear methods, as seen in [CurrenciesManager.svelte.ts](https://github.com/Ayfri/Atom-Clicker/blob/main/src/lib/helpers/CurrenciesManager.svelte.ts):

```ts
export class CurrenciesManager {
	currencies = $state<Record<CurrencyName, {
		amount: number;
		earnedRun: number;
		earnedAllTime: number;
	}>>({} as any);

	add(type: CurrencyName, amount: number) {
		if (amount <= 0) return;
		this.currencies[type].amount += amount;
		this.currencies[type].earnedRun += amount;
		this.currencies[type].earnedAllTime += amount;
	}

	remove(type: CurrencyName, amount: number) {
		if (amount <= 0) return;
		this.currencies[type].amount = Math.max(0, this.currencies[type].amount - amount);
	}
}
```

This removed a lot of repeated logic and made it easier to iterate over all currencies in one place.

## Practical tips I learned

- Put these managers in `.svelte.ts` files, which is where Svelte 5 lets you use runes outside components.
- Export an instance like `export const gameManager = new GameManager();` instead of exporting a `$state` variable that is directly reassigned (which is restricted).
- If you pass a class method directly as a callback, you can lose `this`. Use `() => manager.method()` or an arrow method on the class.
- If a large object does not need deep reactivity, consider [$state.raw](https://svelte.dev/docs/svelte/$state#$state.raw) to avoid proxy overhead.
- Experiment with the [Svelte 5 Playground](https://svelte.dev/playground) to see the compiled output of your classes.

## Wrap-up

Svelte 5 class state let me collapse a tangle of stores into a few straightforward managers.
The refactor in Atom Clicker made the code easier to navigate, and the UI still updates as expected with simple property writes.
If you already like organizing logic in classes, `$state` finally makes that style feel native in Svelte.
