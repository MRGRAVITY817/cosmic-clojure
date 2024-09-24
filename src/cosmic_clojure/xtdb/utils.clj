(ns cosmic-clojure.xtdb.utils
  (:require [clojure.instant :as instant]))

(defn xt-datetime->inst
  "Converts xtdb zoned datetime to java instant"
  [datetime]
  (-> datetime
      (.toInstant)
      (str)
      (instant/read-instant-date)))
