# Cosmic Clojure

Here we learn how to build your Clojure application using TDD, DDD techniques. 

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
