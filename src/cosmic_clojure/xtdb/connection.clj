(ns cosmic-clojure.xtdb.connection
  (:require [xtdb.api :as xt]
            [xtdb.node :as xtn]))

(def db-node (xtn/start-node {}))
