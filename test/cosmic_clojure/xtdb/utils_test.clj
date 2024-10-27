(ns cosmic-clojure.xtdb.utils-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [cosmic-clojure.xtdb.utils :as sut]))

(deftest xt-datetime->inst-test
  (testing "converts xtdb zoned datetime to java instant"
    (let [input  #xt.time/zoned-date-time "2024-09-24T14:08:28.215Z[UTC]"
          output (sut/xt-datetime->inst input)
          _      (is (= #inst "2024-09-24T14:08:28.215-00:00"
                        output))]))
  (testing "converts nil to nil"
    (let [input  nil
          output (sut/xt-datetime->inst input)
          _      (is (= nil output))])))
