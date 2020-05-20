# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]
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
- Renamed subscribe methods that accept listeners to `listen`, which invoke the given listener every time the value changes. In contrast, the `subscribe` method also invokes the given subscriber upon registration. 

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
[1.2.2]: https://github.com/tobiasdiez/EasyBind/compare/v1.0.3...v1.2.2
[1.0.3]: https://github.com/tobiasdiez/EasyBind/releases/tag/v1.0.3
