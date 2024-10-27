(ns cosmic-clojure.batch.interface
  (:require
   [cosmic-clojure.batch.repository :as batch-repo]))

(defn save!
  "Saves a given batch."
  [repo batch]
  (batch-repo/save! repo batch))

(defn get-all
  "Returns all saved batches."
  [repo]
  (batch-repo/get-all repo))

(defn get-by-reference
  "Returns a batch by its reference."
  [repo reference]
  (batch-repo/get-by-reference repo reference))

