# Cosmic Clojure

Here we learn how to build your application using TDD, DDD techniques.  

## Chapter summaries

### Chapter 1: Domain modeling

The chapter needs a fresh project installed with testing library to run the very first unit tests.  

Create a new Clojure project with the following command:

```bash
$ mkdir cosmic-clojure
$ cd cosmic-clojure
$ touch deps.edn
```

Then add testing library and commands to the project by copy and pasting the following code to `deps.edn`:

```clojure
{:aliases {:test {:extra-paths ["test"]
                  :extra-deps {io.github.cognitect-labs/test-runner
                               {:git/tag "v0.5.1" :git/sha "dfb30dd"}}
                  :main-opts ["-m" "cognitect.test-runner"]
                  :exec-fn cognitect.test-runner.api/test}}}
```
