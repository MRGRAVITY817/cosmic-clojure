(ns cosmic-clojure.batch.test-utils
  (:require
   [cosmic-clojure.xtdb.connection :refer [xtdb-client]]
   [cosmic-clojure.xtdb.repositories :as xtdb]))

(defn ->test-batch-repo
  "Creates a batch repository for testing purposes."
  []
  (-> (xtdb-client {:type :in-process})
      (xtdb/batch-repo)))


