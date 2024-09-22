# Cosmic Clojure

Here we learn how to build your Clojure application using TDD, DDD techniques.  

This repo follows the learning path of the book [Cosmic Python](https://www.cosmicpython.com/) by Harry J.W. Percival and Bob Gregory. The difference is that unlike the book, I've writted the code in Clojure, not in Python.

## Chapter 1: Domain modeling

### Setup

Create a new Clojure project with the following command:

```bash
$ mkdir cosmic-clojure
$ cd cosmic-clojure
$ touch deps.edn
```

The chapter needs some basic libraries:
- to write & run tests: `cognitect.test-runner` 
- to modeling the domain data: `malli`  

Copy and paste the following code to `deps.edn`:

```clojure
{:aliases {:test {:exec-fn     cognitect.test-runner.api/test,
                  :extra-deps  {io.github.cognitect-labs/test-runner
                                  {:git/sha "dfb30dd", :git/tag "v0.5.1"}},
                  :extra-paths ["test"],
                  :main-opts   ["-m" "cognitect.test-runner"]}},
 :deps    {metosin/malli       {:mvn/version "0.16.4"},
           org.clojure/clojure {:mvn/version "1.12.0"}},
 :paths   ["src"]}
```

Now you're good to go!

### Summaries

- **Domain** is the problem you're trying to solve. Hence the **domain model** is the structure of the data that would be used to solve the problem.
- In Clojure you can use any schema libraries to build value objects - e.g. malli, schema, etc - since the data is immutable by default.
- When it comes to entities, it might be tempting to override default operators like `==` (for equality checking) and `>` (for comparison and sorting). 
  But operator overriding is not a good idea in Clojure - just create a simple function for each case like `batch-eq?` or `batch-gt?`.

## Chapter 2: Repository Pattern

### Setup

From this chapter, we'll need some kind of database to store our data. I've chosed the [XTDB](https://github.com/xtdb/xtdb), for the sake of it being well adopted among Clojure ecosystem.  

For using XTDB within our project, add those dependencies to `deps.edn`:

```clojure
{:deps {;; ...
        ;; xtdb-api for the main public API, for both remote-client and in-process nodes
        com.xtdb/xtdb-api {:mvn/version "2.0.0-b1"}
        ;; xtdb-http-client-jvm for connecting to a remote server
        com.xtdb/xtdb-http-client-jvm {:mvn/version "2.0.0-b1"}
        ;; xtdb-core for running an in-process (test) node (JDK 21+)
        com.xtdb/xtdb-core {:mvn/version "2.0.0-b1"}}

 ;; JVM options required for in-process node
 :aliases {;; ...
           :dev {:jvm-opts ["--add-opens=java.base/java.nio=ALL-UNNAMED"
                            "-Dio.netty.tryReflectionSetAccessible=true"]}}}
```

We'll use `dev` alias to run our dev repl, running with in-process XTDB node. Run it with the following command:

```bash
$ clj -A:dev
```

Then you can now jack into this repl from your IDE. 


