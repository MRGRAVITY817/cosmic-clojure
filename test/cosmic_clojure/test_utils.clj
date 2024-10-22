(ns cosmic-clojure.test-utils
  (:require
   [cosmic-clojure.handler :refer [app]]
   [cosmic-clojure.xtdb.connection :refer [xtdb-client]]))

(def api-handler
  "An api handler for testing purposes."
  (app {:batch-repo (xtdb-client {:type :in-process})}))
