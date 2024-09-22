(ns cosmic-clojure.db
  (:require [xtdb.api :as xt]
            [xtdb.node :as xtn]))

(def db-node (xtn/start-node {}))

(xt/status db-node)

