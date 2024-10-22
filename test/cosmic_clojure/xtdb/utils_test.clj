(ns cosmic-clojure.xtdb.utils-test
  (:require [clojure.test :refer [deftest is]]
            [cosmic-clojure.xtdb.utils :as sut]))

(deftest xt-datetime->inst-test
  (let [input  #xt.time/zoned-date-time "2024-09-24T14:08:28.215Z[UTC]"
        output (sut/xt-datetime->inst input)
        _      (is (= output #inst "2024-09-24T14:08:28.215-00:00"))]))
