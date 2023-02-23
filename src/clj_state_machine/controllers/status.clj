(ns clj-state-machine.controllers.status
  (:require [clj-state-machine.controllers.utils :as c.utils]
            [clj-state-machine.ports.datomic.core :as datomic.core]
            [clj-state-machine.ports.datomic.status :as datomic.status]
            [datomic-helper.entity :as dh.entity]
            [pedestal-api-helper.params-helper :as p-helper])
  (:import (java.util UUID)))

(defn get-facade
  [id]
  (let [no-id? (nil? id)
        conn (datomic.core/connect!)
        is-uuid? (p-helper/is-uuid id)]
    (cond no-id? (->> (dh.entity/find-all conn :status/id)
                      (c.utils/undefine-entity-keys "status"))
          is-uuid? (->> id
                        UUID/fromString
                        (dh.entity/find-by-id conn :status/id)
                        (c.utils/undefine-entity-keys "status"))
          :else nil)))

(defn upsert-facade
  [status]
  (let [id-from-upsert (datomic.status/upsert! (c.utils/redefine-entity-keys "status" status))]
    {:status 200
     :headers c.utils/headers
     :body {:message ""
            :payload {:id id-from-upsert}}}))

(defn delete-facade
  [_ id]
  (datomic.status/delete! id)
  {:status 204})
