(ns clj-state-machine.models.aux.schema
  (:require [schema.core :as s]))

(defn ^:private valid?
  [schema model]
  (not ((s/checker schema) model)))

(s/defn either
  [& schemas]
  (->> schemas
       (reduce (fn [condition-schemas schema]
                 (conj condition-schemas (partial valid? schema) schema))
               [])
       (apply s/conditional)))
