# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]
### Added
### Changed
### Removed

## [2.2.0] - 2021-05-18
### Added
### Changed
- Mark JavaFX dependency as transitive. Thanks to Sebastian Stenzel.
- Remove `classifier = linux` for JavaFX dependency in the `pom` file. This makes it easier to use the library on different OS without pulling-in the wrong dependencies.

### Removed

## [2.1.0] - 2020-08-13
### Added
- Added support for JDK >= 9. [#21](https://github.com/tobiasdiez/EasyBind/issues/21)
- Added `EasyObservableList#mapped` as fluid alternative to `EasyBind#map`
- Added `EasyObservableList#filteredWrapped` as an alternative to `EasyObservableList#filtered` that returns an `EasyObservableList` instead of a `FilteredList`.
  - This allows fluid method chaining without having to wrap the `FilteredList` using `EasyBind#wrapList` again.
  - It is a temporary workaround that will be removed once https://github.com/openjdk/jfx/pull/278 is merged.

### Changed
- `EasyBind#flatten` and `EasyBind#concat` now return an `EasyObservableList` instead of an `ObservableList`.

### Removed

## [2.0.0] - 2020-07-17
### Added
- Added new interface `EasyObservableList` which is a wrapper around a standard `ObservableList` providing access to a few convenient helper methods. Use `EasyBind.wrapList` to create such a wrapper around a given `ObservableList`.
- Added new method `EasyBind.reduce(list, accumulation)` that creates a binding holding the result of the accumulation function on the provided list.
- Added a few new methods `EasyBind.valueAt` that are essentially equivalent to the standard `Bindings.valueAt` methods except that they gracefully handle non-existing values by returning a `OptionalBinding`.
- Completely reworked bindings for optional values: 
  - New methods in `ObervableOptionalValue` having `Value` in the name provide convenient access to the corresponding method of an optional. For example, `getValueOrElse` and `isValuePresent` are analogs of `Optional.getOrElse` and `Optional.isPresent`.
  - New methods that create bindings whose value is computed similar to the corresponding methods in `Optional`, e.g. `isPresent`, `isEmpty`, `orElse`, `flatMap`.

### Changed
- Renamed package to `com.tobiasdiez.easybind`.
- Split `MonadicBinding` into a part which is really concerned with optionals (new `ObervableOptionalValue`) and one which provides helper methods for a fluent interface for normal bindings (new `EasyBinding`).
- Renamed `flatMap` to `mapObservable` to avoid clashes with the corresponding method in `Optional`.
- The `mapObservable` and `selectProperty` now invoke the mapper even if the source observable value holds a `null` value. The corresponding methods in `ObservableOptionalValue` allow for a more convenient `null` handling.
- Renamed subscribe methods that accept listeners to `listen`, which invoke the given listener every time the value changes. In contrast, the `subscribe` method also invokes the given subscriber upon registration. 
- Renamed `EasyBind.listBind` to `bindContent` in order to align with the naming of `Bindings.bindContent`.
- Moved `PropertyBinding` to `com.tobiasdiez.easybind`.

### Removed
- Removed `EasyBind.filter(ObservableValue<T> source, Predicate<? super T> predicate)`. Use `EasyBind.wrapNullable(source).filter(predicate)` instead. 
- Removed `EasyBind.orElse(ObservableValue<? extends T> source, T other)`. Use `EasyBind.wrapNullable(source).orElse(other)` instead. 


## [1.2.2] - 2020-05-05
### Added
- `EasyBind.flatten(List<ObservableList<T>> lists)` and `EasyBind.concat(ObservableList<T>... lists)` that create a new list combining the values of the given lists. Unlike `FXCollections.concat()`, updates to the source lists propagate to the combined list.
- `EasyBind.mapBacked(ObservableList<A> source, Function<A, B> mapper)` that works similar to `EasyBind.map` with the important difference that the `mapper` is applied only on initializing the list and not every time an item in the list is accessed.
- `EasyBind.filter`
- `EasyBind.flatMap`
- `EasyBind.selectProperty`
- `EasyBind.orElse`
- Support for Java Module System by providing correct module information.

## [1.0.3] - 2014-08-19
Last official release by [Tomas Mikula](https://github.com/TomasMikula/EasyBind) under the name `org.fxmisc.easybind:easybind`.


[Unreleased]: https://github.com/tobiasdiez/EasyBind/compare/v1.2.2...master
[2.2.0]: https://github.com/tobiasdiez/EasyBind/compare/2.1.0...2.2.0
[2.1.0]: https://github.com/tobiasdiez/EasyBind/compare/2.0.0...2.1.0
[2.0.0]: https://github.com/tobiasdiez/EasyBind/compare/v1.2.2...2.0.0
[1.2.2]: https://github.com/tobiasdiez/EasyBind/compare/v1.0.3...v1.2.2
[1.0.3]: https://github.com/tobiasdiez/EasyBind/releases/tag/v1.0.3
