# Nested Photo-Fragments for Android
This project demonstrates how to use multiple nested-inner fragments from Android's support library, including how to handle implicit intent calls and return subsequent responses back to the appropriate fragment. It also shows how to handle bitmaps within nested-inner fragments to prevent memory leaks on rotation/exit.

## Screenshot
![alt tag](https://cloud.githubusercontent.com/assets/12819143/9178359/dfb70fa6-3f64-11e5-9f85-fa1542ab84a1.gif "Nested Photo-Fragments")

## Motivation
Due to the buggy nature of nested fragments in the support library their use is somewhat discouraged and there isn't much online about how to properly implement them, and Google still has (as of this push) a number of open tickets on various issues. This code is part of a larger project I'm developing on and working around these issues took a while to figure out, so here's how to do it. I provide more details into these issues on my [website](http://matthewgusella.com)

## Installation
Just download and run, min-sdk: 15, only dependency is the support library (currently v7:22.2.0)

## Contributors
I welcome any contributors, if you find better ways to implement this please share. 
