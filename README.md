[![Build Status](https://travis-ci.org/raulccabreu/redukt.svg?branch=master)](https://travis-ci.org/raulccabreu/redukt)
[![](https://jitpack.io/v/raulccabreu/redukt.svg)](https://jitpack.io/#raulccabreu/redukt)

# redukt
Redux architecture pattern to Android writed in Kotlin

## Add as dependency

Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency
```gradle
dependencies {
    compile 'com.github.raulccabreu:redukt:-SNAPSHOT'
}
```

## Basic usage

```kotlin
class CounterReducer : Reducer<Integer> {
    override fun reduce(state: Int, action: Action<*>): Int {
        if (action.name == "INC") return state + 1
        if (action.name == "DEC") return state - 1
        return state
    }
}

val redukt = Redukt<Int>(0)
redukt.reducers.add(CounterReducer())
redukt.listeners.add(object: StateListener<String> {
    override fun hasChanged(newState: Int, oldState: Int) = newState != oldState
    override fun onChanged(state: Int) { println("count: $state") }
})

redukt.dispatch(Action("INC"))
redukt.dispatch(Action("DEC"))
redukt.stop()
```

#### MIT License

Copyright (c) 2017 Raul Abreu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
