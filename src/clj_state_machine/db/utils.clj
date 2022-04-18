(ns clj-state-machine.db.utils
  (:require [schema.core :as s])
  (:use clojure.pprint))

(defn props-from-value
  [value]
  (if (vector? value)
    (merge {:db/cardinality :db.cardinality/many}
           (props-from-value (first value)))
    (cond (= value java.util.UUID) {:db/valueType :db.type/uuid
                                    :db/unique :db.unique/identity}
          (= value s/Str) {:db/valueType :db.type/string}
          (= value BigDecimal) {:db/valueType :db.type/bigdec}
          (= value Long) {:db/valueType :db.type/long}
          (= value s/Keyword) {:db/valueType :db.type/keyword}
          (map? value) {:db/valueType :db.type/ref}
          (= value s/Bool) {:db/valueType :db.type/boolean}
          :else {:error "unknown" :type (type value) :class (class value)}
          ))
  )

(defn has-element-by-keyword?
  [el map keyword]
  (cond (empty? map) false
        (.contains
          (get map keyword []) el) true
        :else false))

(defn is-indexed?
  [key configs]
  (has-element-by-keyword? key configs :indexed))

(defn is-component?
  [key configs]
  (has-element-by-keyword? key configs :components))

(defn is-historyless?
  [key configs]
  (has-element-by-keyword? key configs :historyless))

(defn extract-key
  [key]
  (cond (keyword? key) key
        (instance? schema.core.OptionalKey key) (get key :k)
        :else key))

(defn assoc-when-element-predicate-of-config
  [map-to-assoc configs config-element predicate? key value]
  (if (predicate? config-element configs)
    (assoc map-to-assoc key value)
    map-to-assoc))

(defn extract-key-extra-props-from-configs
  [key configs]
  (let [extra-props {}
        extra-props (assoc-when-element-predicate-of-config extra-props configs key is-indexed? :db/index true)
        extra-props (assoc-when-element-predicate-of-config extra-props configs key is-component? :db/isComponent true)
        extra-props (assoc-when-element-predicate-of-config extra-props configs key is-historyless? :db/noHistory true)]
    extra-props))

(defn keyvalue-to-def
  [key value configs]
  (let [extracted-key (extract-key key)
        base {:db/ident extracted-key
              :db/cardinality :db.cardinality/one}
        extra (merge
                (props-from-value value)
                (extract-key-extra-props-from-configs extracted-key configs))]
    (merge base extra)))

(defn map-schema-to-datomic
  [[key value]]
  {:key key :value value})

(defn schema-to-datomic
  "converts {} schema to [{}...] datomic schema to be inserted into the database,
  the configs are optional, with optional keys, as following
  {:indexed [:ks...] :components [:ks...] :historyless [:ks...]}
  a key sent in the list of the configs will have
  in :indexed = db/indexed true
  in :components = db/isComponent true
  in :historyless = db/noHistory true"
  ([definition configs]
   (->> definition
        (mapv map-schema-to-datomic)
        (reduce (fn [el1 el2]
                  (cond (map? el1)
                        (conj [(keyvalue-to-def (:key el1) (:value el1) configs)]
                                (keyvalue-to-def (:key el2) (:value el2) configs))
                        :else (conj el1 (keyvalue-to-def (:key el2) (:value el2) configs)))
                  ))))
  ([definition]
    (schema-to-datomic definition {})))

(defn schemas-to-datomic
  "converts a col of definitions into datomic schema"
  ([definitions configs]
    (reduce (fn [col definition]
              (let [converted (schema-to-datomic definition configs)]
                (println "converted" converted)
                (concat col converted)))
            []
            definitions))
  ([definitions]
   (schemas-to-datomic definitions {})))