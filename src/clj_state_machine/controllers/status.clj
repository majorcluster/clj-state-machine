(ns clj-state-machine.controllers.status
  (:require [clj-state-machine.controllers.utils :as controllers.utils]
            [clj-state-machine.models.status :as models.status]
            [clj-state-machine.ports.datomic.status :as datomic.status]
            [schema.core :as s]))

(s/defn get-facade :- (s/cond-pre [models.status/StatusDef] models.status/StatusDef)
  [id :- (s/maybe s/Uuid)]
  (let [no-id? (nil? id)]
    (cond no-id? (datomic.status/find-all)
          :else (datomic.status/find-one id))))

(defn upsert-facade
  [status]
  (let [id-from-upsert (datomic.status/upsert! (controllers.utils/redefine-entity-keys "status" status))]
    {:status  200
     :headers controllers.utils/headers
     :body    {:message ""
               :payload {:id id-from-upsert}}}))

(defn delete-facade
  [_ id]
  (datomic.status/delete! id)
  {:status 204})
