(ns clj-state-machine.controller.status
  (:require [clj-state-machine.controller.utils :as c.utils]
            [clj-state-machine.db.entity :as db.entity]
            [clj-state-machine.db.config :as db.config]
            [clj-state-machine.db.status :as db.status])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(defn get-facade
  [id]
  (let [is-uuid (c.utils/is-uuid id)]
    (cond is-uuid (->> id
                       UUID/fromString
                       (db.entity/find-by-id (db.config/connect!) :status/id)
                       (c.utils/undefine-entity-keys "status"))
          :else nil)))

(defn upsert-facade
  [status]
  (let [id-from-upsert (db.status/upsert! (c.utils/redefine-entity-keys "status" status))]
    {:status 200
     :headers c.utils/headers
     :body {:message ""
            :payload {:id id-from-upsert}}}))

(defn delete-facade
  [language id]
  (db.status/delete! id)
  {:status 204})