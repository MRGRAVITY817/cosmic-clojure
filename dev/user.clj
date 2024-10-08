(ns user
  (:require
   [cosmic-clojure.system :as system]
   [integrant.repl :as ig-repl])
  (:gen-class))

(ig-repl/set-prep! (fn [] system/config))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(comment
  (require 'user)
  (go)
  (halt)
  (reset)
  (reset-all))

