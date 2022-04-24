(ns clj-state-machine.controller.status
  (:require [clj-state-machine.controller.utils :as c.utils]
            [clj-state-machine.db.entity :as db.entity]
            [clj-state-machine.db.config :as db.config]
            [clj-state-machine.db.status :as db.status]
            [pedestal-api-helper.params-helper :as p-helper])
  (:use clojure.pprint)
  (:import (java.util UUID)))

(defn get-facade
  [id]
  (let [no-id? (nil? id)
        conn (db.config/connect!)
        is-uuid? (p-helper/is-uuid id)]
    (cond no-id? (->> (db.entity/find-all conn :status/id)
                      (c.utils/undefine-entity-keys "status"))
          is-uuid? (->> id
                       UUID/fromString
                       (db.entity/find-by-id conn :status/id)
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