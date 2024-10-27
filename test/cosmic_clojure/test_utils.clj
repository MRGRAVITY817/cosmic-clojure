(ns cosmic-clojure.test-utils
  (:require
   [cosmic-clojure.handler :refer [app]]
   [cosmic-clojure.xtdb.connection :refer [xtdb-client]]
   [cosmic-clojure.xtdb.repositories :as xtdb]))

(defn api-handler
  "An api handler for testing purposes."
  [& [{:keys [batch-repo]}]]
  (let [xtdb-client (xtdb-client {:type :in-process})]
    (app {:batch-repo (or batch-repo (xtdb/batch-repo xtdb-client))})))
