(ns cosmic-clojure.xtdb.connection
  (:require [xtdb.client :as xtc]
            [xtdb.node :as xtn]))

(defn xtdb-client
  "Creates a new xtdb client from given info."
  [{:keys [type url]}]
  (case type
    :in-process
    (xtn/start-node {})

    :remote
    (xtc/start-client url)))
