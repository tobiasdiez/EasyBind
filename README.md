# EasyBind

EasyBind leverages lambdas to reduce boilerplate when creating custom bindings, provides a type-safe alternative to `Bindings.select*` methods and adds provides enhanced bindings support for `Optional`.

See below for [how to install EasyBind in your project](#use-easybind-in-your-project).

This is a maintained fork of the [EasyBind library by Tomas Mikula](https://github.com/TomasMikula/EasyBind), which sadly is dormant at the moment.

## Getting started

The simplest way is to use the `EasyBind.wrap*` methods to create wrappers around standard JavaFX observable values or lists.
The wrapper then gives you access to all the features of EasyBind.
For example,
```java
ObservableStringValue str = ...;
Binding<Integer> length = EasyBind.wrap(str)
                                  .map(String::length);
```
creates a `Binding` that holds the length of `str`.
Similarly, 
```java
ObservableList<String> list = ...;
Binding<Boolean> allMatch = EasyBind.wrap(list)
                                    .allMatch(String:isEmpty) 
```
yields a `Binding` that reflects whether all items in the list are empty strings.
In addition to the `wrap*` methods, EasyBind also provides direct shortcuts for common functionality.
For example, the above binding could be more shortly written as `EasyBind.map(String::length)`.

## Features
### Map observables

Creates a binding whose value is a mapping of some observable value.

```java
ObservableStringValue str = ...;
Binding<Integer> lenght = EasyBind.map(str, String::length);
```

### Combine observables

Creates a binding whose value is a combination of two or more observable values.

```java
ObservableStringValue str = ...;
ObservableValue<Integer> start = ...;
ObservableValue<Integer> end = ...;
Binding<String> substring = EasyBind.combine(str, start, end, String::substring);
```

### Select properties

Type-safe alternative to `Bindings.select*` methods.
```java
Binding<Boolean> showing = EasyBind.select(control.sceneProperty()) 
                                   .select(scene -> scene.windowProperty()) 
                                   .selectObject(window -> window.showingProperty());
```
The resulting binding is updated whenever one of the properties in the selection graph changes.

### Map items in a lists

Returns a mapped view of an `ObservableList`.
```java
ObservableList<String> items = ...;
ObservableList<Integer> lengths = EasyBind.map(items, String::getLength);
```
In the above example, `lenghts` is updated as elements are added and removed from `items`.
By design, the elements of the new observable are calculated on the fly whenever they are needed (e.g. if `get` is called).
Thus, this is prefect for light-weight operations.
If the conversion is a cost-intensive operation or if the elements of the list are often accessed, then using `mapBacked` is a better option.
Here the elements of the list are converted once and then stored in memory.

### Reduce observable lists
Using `reduce` you can aggregate an observable list of items into a single observable value.

```java
ObservableList<String> items = ...;
ObservableValue<Boolean> totalLength = EasyBind.reduce(items, stream -> stream.mapToInt(String::length).sum());
``` 

### Reduce observable lists of observables

More advanced, the `combine` method turns an _observable list_ of _observable values_ into a single observable value. The resulting observable value is updated whenever elements are added or removed to or from the list, as well as when element values change.

```java
Property<Integer> a = new SimpleObjectProperty<>(5);
Property<Integer> b = new SimpleObjectProperty<>(10);
ObservableList<Property<Integer>> list = FXCollections.observableArrayList();

Binding<Integer> sum = EasyBind.combine(
        list,
        stream -> stream.reduce((a, b) -> a + b).orElse(0));

assert sum.getValue() == 0;

// sum responds to element additions
list.add(a);
list.add(b);
assert sum.getValue() == 15;

// sum responds to element value changes
a.setValue(20);
assert sum.getValue() == 30;

// sum responds to element removals
list.remove(a);
assert sum.getValue() == 10;
```

You don't usually have an observable list of _observable_ values, but you often have an observable list of something that _contains_ an observable value. In that case, use the above `map` methods to get an observable list of observable values, as in the example below.

<details>
<summary>
Example: Disable "Save All" button on no unsaved changes
</summary>
Assume a tab pane that contains a text editor in every tab. The set of open tabs (i.e. open files) is changing. Let's further assume we use a custom `Tab` subclass `EditorTab` that has a boolean `savedProperty()` indicating whether changes in its editor have been saved.

**Task:** Keep the _"Save All"_ button disabled when there are no unsaved changes in any of the editors.

```java
ObservableList<ObservableValue<Boolean>> individualTabsSaved =
        EasyBind.map(tabPane.getTabs(), tab -> ((EditorTab) tab).savedProperty());

ObservableValue<Boolean> allTabsSaved = EasyBind.combine(
        individualTabsSaved,
        stream -> stream.allMatch(saved -> saved));

Button saveAllButton = new Button(...);
saveAllButton.disableProperty().bind(allTabsSaved);
```
</details>


### Concat lists (of observable lists)
The `concat` method combines two or more observable lists into one big list containing all items.
```java
ObservableList<String> listA = ...;
ObservableList<String> listB = ...;
ObservableList<String> combinedList = EasyBind.concat(listA, listB);
```

Similarly, an observable list of observable lists can be combined into one big list containing all items of all lists as follows:
```java
ObservableList<ObservableList<String>> listOfLists = ...;
ObservableList<String> allItems = EasyBind.flatten(listOfLists);
```

### Subscribe to values

Often one wants to execute some code for _each_ value of an `ObservableValue`, that is for the _current_ value and _each new_ value. This typically results in code like this:

```java
this.doSomething(observable.getValue());
observable.addListener((obs, oldValue, newValue) -> this.doSomething(newValue));
```

This can be expressed more concisely using the `subscribe` helper method:

```java
EasyBind.subscribe(observable, this::doSomething);
```

In case `doSomething` should not be invoked immediately, `EasyBind.listen(observable, this::doSomething)` should be used instead.

### Conditional bindings
Using `when` you can create bindings that should only be realized if a given observable boolean is true.

#### Conditional collection membership

The method `includeWhen` includes an element in a collection based on a boolean condition.

Say that you want to draw a line and highlight it when its hovered over. To achieve this, let's add `.highlight` CSS class to the line node when it is hovered over and remove it when it is not:

```java
EasyBind.includeWhen(edge.getStyleClass(), "highlight", line.hoverProperty());
```

### Optional observable values

One often faces the situation that observables take `null` values. 
The `wrapNullable` provides a wrapper around the observable that provides convenient helper methods similar to the `Optional` class.

```java
interface ObservableOptionalValue<T> {
    BooleanBinding isPresent();
    BooleanBinding isEmpty();
    Subscription listenToValues(SimpleChangeListener<? super T> listener);
    Subscription subscribeToValues(Consumer<? super T> subscriber);
    EasyBinding<T> orElse(T other);
    OptionalBinding<T> orElse(ObservableValue<T> other);
    OptionalBinding<T> filter(Predicate<? super T> predicate);
    OptionalBinding<U> map(Function<? super T, ? extends U> mapper);
    OptionalBinding<U> flatMap(Function<T, Optional<U>> mapper);
    PropertyBinding<U> selectProperty(Function<? super T, O> mapper);
    ...
}
```

Example:

```java
BooleanBinding currentTabHasContent = EasyBind.wrapNullable(tabPane.getSelectionModel().selectedItemProperty())
                                              .map(Tab::contentProperty)
                                              .isPresent();
```


The `EasyBind.valueAt(list, index)` and `EasyBind.valueAt(map, key)` methods return a binding containing the item at the given position in the list or map.
The returned binding will be empty if the index/key points behind the list (or at a `null` item). 

Use EasyBind in your project
----------------------------

### Stable release

Current stable release is `2.1.0`.
It contains many new features, but also breaks backwards compatibility to the `1.x` versions as many methods have been renamed; see the [Changelog](CHANGELOG.md) for details.
In case you are upgrading from the `EasyBind` library developed by by Tomas Mikula, then the easiest option is to use version `1.2.2` which includes a few improvements and bug fixes while being compatible with older versions.

#### Maven coordinates

| Group ID            | Artifact ID | Version |
| :-----------------: | :---------: | :-----: |
| com.tobiasdiez      | easybind    | 2.1.0   |

#### Gradle example

```groovy
dependencies {
    compile group: 'com.tobiasdiez', name: 'easybind', version: '2.1.0'
}
```

#### Sbt example

```scala
libraryDependencies += "com.tobiasdiez" % "easybind" % "2.1.0"
```

#### Manual download

[Download](https://github.com/tobiasdiez/EasyBind/releases) the JAR file and place it on your classpath.


### Snapshot releases

Snapshot releases are deployed to Sonatype snapshot repository.

#### Maven coordinates

| Group ID            | Artifact ID | Version        |
| :-----------------: | :---------: | :------------: |
| com.tobiasdiez      | easybind    | 2.1.1-SNAPSHOT |

#### Gradle example

```groovy
repositories {
    maven {
        url 'https://oss.sonatype.org/content/repositories/snapshots/' 
    }
}

dependencies {
    compile group: 'com.tobiasdiez', name: 'easybind', version: '2.1.1-SNAPSHOT'
}
```

#### Sbt example

```scala
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.tobiasdiez" % "easybind" % "2.1.1-SNAPSHOT"
```

#### Manual download

[Download](https://oss.sonatype.org/content/repositories/snapshots/com/tobiasdiez/easybind/) the latest JAR file and place it on your classpath.

