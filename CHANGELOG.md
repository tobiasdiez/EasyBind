# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]
### Added
### Changed
- Renamed package to `com.tobiasdiez.easybind`
### Removed

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
