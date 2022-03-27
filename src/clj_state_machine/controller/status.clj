(ns clj-state-machine.controller.status
  (:require [schema.core :as s]
            [clj-state-machine.model.status :as model.status]
            [clj-state-machine.controller.utils :as c.utils])
  (:use clojure.pprint))

(s/defn insert-status
  [status :- (model.status/StatusPostDef)])

(defn insert-status-facade
  [status]
  (try
      (insert-status status)
    (catch Exception e
      (pprint e)
      c.utils/common-messages)))