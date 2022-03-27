(ns clj-state-machine.db.utils-test
  (:require [clojure.test :refer :all]
            [clj-state-machine.db.utils :refer :all]
            [schema.core :as s])
  (:use [clojure.pprint]))

(defn- MangoProducerDef
  []
  {:mango-producer/id s/Uuid
   (s/optional-key :mango-producer/name) s/Str
   (s/optional-key :mango-producer/family-size) Long
   (s/optional-key :mango-producer/average-mangoes) BigDecimal
   (s/optional-key :mango-producer/mango-types) [s/Keyword]
   (s/optional-key :mango-producer/produces-other-goods?) s/Bool})

(defn- MangoCooperativeDef
  []
  {:mango-cooperative/id s/Uuid
   (s/optional-key :mango-cooperative/name) s/Str
   (s/optional-key :mango-cooperative/producers) [(MangoProducerDef)]})

(defn- uuid-datomic-def
  [ident-key]
  {:db/ident ident-key
  :db/valueType :db.type/uuid
  :db/cardinality :db.cardinality/one
  :db/unique :db.unique/identity})

(defn- string-datomic-def
  [ident-key]
  {:db/ident ident-key
   :db/valueType :db.type/string
   :db/cardinality :db.cardinality/one})

(defn- long-datomic-def
  [ident-key]
  {:db/ident ident-key
   :db/valueType :db.type/long
   :db/cardinality :db.cardinality/one})

(defn- bigdec-datomic-def
  [ident-key]
  {:db/ident ident-key
   :db/valueType :db.type/bigdec
   :db/cardinality :db.cardinality/one})

(defn- bigdec-datomic-index-def
  [ident-key]
  (assoc (bigdec-datomic-def ident-key) :db/index true))

(defn- keyword-datomic-def
  [ident-key]
  {:db/ident ident-key
   :db/valueType :db.type/keyword
   :db/cardinality :db.cardinality/one})

(defn- keyword-col-datomic-def
  [ident-key]
  {:db/ident ident-key
   :db/valueType :db.type/keyword
   :db/cardinality :db.cardinality/many})

(defn- bool-datomic-def
  [indent-key]
  {:db/ident indent-key
   :db/valueType :db.type/boolean
   :db/cardinality :db.cardinality/one})

(defn- ref-datomic-def
  [ident-key]
  {:db/ident ident-key
   :db/valueType :db.type/ref
   :db/cardinality :db.cardinality/one})

(defn- ref-col-datomic-def
  [ident-key]
  {:db/ident ident-key
   :db/valueType :db.type/ref
   :db/cardinality :db.cardinality/many})

(defn- ref-col-datomic-component-def
  [ident-key]
  (assoc (ref-col-datomic-def ident-key) :db/isComponent true))

(defn- bool-datomic-historyless-def
  [indent-key]
  (assoc (bool-datomic-def indent-key) :db/noHistory true))

(defn- MangoProducerDatomicDef
  []
  [(uuid-datomic-def :mango-producer/id)
   (string-datomic-def :mango-producer/name)
   (long-datomic-def :mango-producer/family-size)
   (bigdec-datomic-index-def :mango-producer/average-mangoes)
   (keyword-col-datomic-def :mango-producer/mango-types)
   (bool-datomic-historyless-def :mango-producer/produces-other-goods?)])

(defn- MangoCooperativeDatomicDef
  []
  [(uuid-datomic-def :mango-cooperative/id)
   (string-datomic-def :mango-cooperative/name)
   (ref-col-datomic-component-def :mango-cooperative/producers)])

(deftest has-element-by-keyword-test?
  (testing "gives true when has keyword as array containing element"
    (is (has-element-by-keyword? 1 {:test [1]} :test))
    (is (has-element-by-keyword? 1 {:test [1,5,3]} :test))
    (is (has-element-by-keyword? 1 {:test [1] :something-else [1]} :test))
    (is (has-element-by-keyword? 1 {:test [1] :something-else [1]} :something-else)))
  (testing "gives false when is empty or array is empty"
    (is (not (has-element-by-keyword? 1 {} :test)))
    (is (not (has-element-by-keyword? 1 {:test []} :test)))
    (is (not (has-element-by-keyword? 1 {:test [] :something-else [1]} :test)))
    )
  (testing "gives false when is not present in array"
    (is (not (has-element-by-keyword? "1" {:test ["2", "3", "10"]} :test)))
    (is (not (has-element-by-keyword? "1" {:test ["4" "-1"] :something-else ["1"]} :test)))
    (is (not (has-element-by-keyword? 1 {:test ["1"] :something-else ["1"]} :test)))
    (is (not (has-element-by-keyword? 1 {:test [4,-1] :something-else []} :test)))
    ))

(deftest assoc-when-element-predicate-of-config-test
  (let [simple-true-predicate (fn [_ _] true)
        simple-false-predicate (fn [_ _] false)
        simple-config {:indexed [3]}
        longer-config {:indexed ["my-key"] :another-list [1,2]}
        has-el-by-keyword-predicate (fn [key configs]
                                      (has-element-by-keyword? key configs :indexed))]
   (testing "when predicate returns true assoc"
    (is (= {:old [1200] :new [-90]}
           (assoc-when-element-predicate-of-config {:old [1200]} {} 1 simple-true-predicate :new [-90])))
    (is (= {:new [-90]}
           (assoc-when-element-predicate-of-config {} {} 1 simple-true-predicate :new [-90])))
    (is (= {:new [-90]}
           (assoc-when-element-predicate-of-config {:new [-90]} {} 1 simple-true-predicate :new [-90])))
    )
   (testing "when has-element-by-keyword? returns true assoc"
     (is (= {:old [1200] :new [-90]}
            (assoc-when-element-predicate-of-config {:old [1200]} simple-config 3 has-el-by-keyword-predicate :new [-90])))
     (is (= {:old [1200, "-90"] :new [-90]}
            (assoc-when-element-predicate-of-config {:old [1200, "-90"]} simple-config 3 has-el-by-keyword-predicate :new [-90])))
     (is (= {:new [-90]}
            (assoc-when-element-predicate-of-config {:new [-90]} simple-config 3 has-el-by-keyword-predicate :new [-90])))
     (is (= {:new [-90]}
            (assoc-when-element-predicate-of-config {:new [-90]} longer-config "my-key" has-el-by-keyword-predicate :new [-90])))
     )
   (testing "when predicate returns false dont assoc"
     (is (= {:old [-4000]}
            (assoc-when-element-predicate-of-config {:old [-4000]} {} 1 simple-false-predicate :new [-90])))
     (is (= {}
            (assoc-when-element-predicate-of-config {} {} 1 simple-false-predicate :new [-90])))
     (is (= {:new ["8500"]}
            (assoc-when-element-predicate-of-config {:new ["8500"]} {} 1 simple-false-predicate :new [-90])))
     )
   (testing "when has-element-by-keyword? returns false dont assoc"
     (is (= {:old [1200]}
            (assoc-when-element-predicate-of-config {:old [1200]} simple-config -1 has-el-by-keyword-predicate :new [-90])))
     (is (= {:old [1200, "-90"] }
            (assoc-when-element-predicate-of-config {:old [1200, "-90"]} simple-config 90 has-el-by-keyword-predicate :new [-90])))
     (is (= {:new [-90]}
            (assoc-when-element-predicate-of-config {:new [-90]} simple-config "other-key" has-el-by-keyword-predicate :new [-90])))
     (is (= {:new [-90]}
            (assoc-when-element-predicate-of-config {:new [-90]} longer-config "other-key" has-el-by-keyword-predicate :new [-90])))
     )
   ))

(deftest extract-key-extra-props-from-configs-test
  (let [extra-props-indexed {:db/index true}
        extra-props-index-and-component {:db/index true
                                         :db/isComponent true}
        simple-idx-config {:indexed ["my-key"]}
        simple-idx-n-comp-config {:indexed ["my-key"] :components ["my-key"]}]
   (testing "adding indexed when configured"
    (is (= (extract-key-extra-props-from-configs "my-key", simple-idx-config)
           extra-props-indexed))
    (is (= (extract-key-extra-props-from-configs "my-key", simple-idx-n-comp-config)
           extra-props-index-and-component)))
   (testing "not adding when not configured"
     (is (= (extract-key-extra-props-from-configs "other-key", simple-idx-config)
            {}))
     (is (= (extract-key-extra-props-from-configs "other-key", simple-idx-n-comp-config)
            {})))))

(deftest keyvalue-to-def-test
  (let [extra-props-indexed {:db/index true}
        extra-props-index-and-component {:db/index true
                                         :db/isComponent true}
        simple-idx-config {:indexed ["my-key"]}
        simple-idx-n-comp-config {:indexed ["my-key"] :components ["my-key"]}]
    (testing "fields without extra configs are converted"
      (is (= (keyvalue-to-def :my-id s/Uuid {})
             (uuid-datomic-def :my-id)))
      (is (= (keyvalue-to-def :my-string s/Str {})
             (string-datomic-def :my-string)))
      (is (= (keyvalue-to-def :my-long Long {})
             (long-datomic-def :my-long)))
      (is (= (keyvalue-to-def :my-bigdec BigDecimal {})
             (bigdec-datomic-def :my-bigdec)))
      (is (= (keyvalue-to-def :my-keyword s/Keyword {})
             (keyword-datomic-def :my-keyword)))
      (is (= (keyvalue-to-def :my-keyword-col [s/Keyword] {})
             (keyword-col-datomic-def :my-keyword-col)))
      (is (= (keyvalue-to-def :my-bool s/Bool {})
             (bool-datomic-def :my-bool)))
      (is (= (keyvalue-to-def :my-ref (MangoProducerDef) {})
             (ref-datomic-def :my-ref)))
      (is (= (keyvalue-to-def :my-ref-col [(MangoProducerDef)] {})
             (ref-col-datomic-def :my-ref-col)))
      )
    (testing "fields with extra configs are coverted"
      (is (= (keyvalue-to-def :my-bigdec BigDecimal {:indexed [:my-bigdec]})
             (bigdec-datomic-index-def :my-bigdec)))
      (is (= (keyvalue-to-def :my-ref [(MangoProducerDef)] {:components [:my-ref]})
             (ref-col-datomic-component-def :my-ref)))
      (is (= (keyvalue-to-def :my-bool s/Bool {:historyless [:my-bool]})
             (bool-datomic-historyless-def :my-bool))))
    (testing "optional fields without extra configs are converted"
      (is (= (keyvalue-to-def (s/optional-key :my-id) s/Uuid {})
             (uuid-datomic-def :my-id)))
      (is (= (keyvalue-to-def (s/optional-key :my-string) s/Str {})
             (string-datomic-def :my-string)))
      (is (= (keyvalue-to-def (s/optional-key :my-long) Long {})
             (long-datomic-def :my-long)))
      (is (= (keyvalue-to-def (s/optional-key :my-bigdec) BigDecimal {})
             (bigdec-datomic-def :my-bigdec)))
      (is (= (keyvalue-to-def (s/optional-key :my-keyword) s/Keyword {})
             (keyword-datomic-def :my-keyword)))
      (is (= (keyvalue-to-def (s/optional-key :my-keyword-col) [s/Keyword] {})
             (keyword-col-datomic-def :my-keyword-col)))
      (is (= (keyvalue-to-def (s/optional-key :my-bool) s/Bool {})
             (bool-datomic-def :my-bool)))
      (is (= (keyvalue-to-def (s/optional-key :my-ref) (MangoProducerDef) {})
             (ref-datomic-def :my-ref)))
      (is (= (keyvalue-to-def (s/optional-key :my-ref-col) [(MangoProducerDef)] {})
             (ref-col-datomic-def :my-ref-col))))
    (testing "optional fields with extra configs are coverted"
      (is (= (keyvalue-to-def (s/optional-key :my-bigdec) BigDecimal {:indexed [:my-bigdec]})
             (bigdec-datomic-index-def :my-bigdec)))
      (is (= (keyvalue-to-def (s/optional-key :my-ref) [(MangoProducerDef)] {:components [:my-ref]})
             (ref-col-datomic-component-def :my-ref)))
      (is (= (keyvalue-to-def (s/optional-key :my-bool) s/Bool {:historyless [:my-bool]})
             (bool-datomic-historyless-def :my-bool))))
   ))

(deftest schema-to-datomic-test
  (testing "MangoCooperative schema to datomic works?"
    (is (= (schema-to-datomic (MangoCooperativeDef) {:components [:mango-cooperative/producers]})
           (MangoCooperativeDatomicDef)))
    )
  (testing "MangoProducer schema to datomic works?"
    (is (= (schema-to-datomic (MangoProducerDef) {:indexed [:mango-producer/average-mangoes]
                                                  :historyless [:mango-producer/produces-other-goods?]})
           (MangoProducerDatomicDef)))
    ))


