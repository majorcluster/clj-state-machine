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

(s/defn upsert-facade :- s/Uuid
  [status :- models.status/StatusInputDef]
  (datomic.status/upsert! status))

(s/defn delete-facade
  [_ :- s/Keyword
   id :- s/Uuid]
  (datomic.status/delete! id))
