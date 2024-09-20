(ns cosmic-clojure.batches-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [cosmic-clojure.batches :as batches]))

(defn- now [] (new java.util.Date))

(deftest batches-tests
  (testing "allocating to a batch reduces the available quantity"
    (let [;; Arrange 
          batch #:Batch{:reference "batch-001"
                        :sku       "SMALL-TABLE"
                        :quantity  20
                        :eta       (now)}
          line  #:OrderLine{:order-id "order-ref"
                            :sku      "SMALL-TABLE"
                            :quantity 2}
         ;; Act
          output (batches/allocate-line batch line)]
     ;; Assert
      (is (= (batches/available-quantity output) 18)))))

(comment
  (clojure.test/run-tests)
  :rcf)
