(ns clj-state-machine.ports.datomic.transition
  (:require [clj-state-machine.models.transition :as models.transition]
            [clj-state-machine.ports.datomic.core :as datomic.core]
            [datomic-helper.entity :as dh.entity]
            [datomic.api :as d]
            [pedestal-api-helper.params-helper :as p-helper]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn upsert! :- s/Uuid
  [workflow-id :- s/Uuid
   entity :- models.transition/TransitionInputDef]
  (let [conn (datomic.core/connect!)
        id (:transition/id entity)
        id (cond id id
                 :else (p-helper/uuid))
        complete (assoc entity :transition/id id)]
    (dh.entity/upsert! conn [:transition/id id] complete)
    (dh.entity/upsert-foreign!
     conn
     :transition/id
     id
     :workflow/transitions
     :workflow/id
     workflow-id)
    id))

(s/defn delete!
  [id :- s/Uuid]
  (let [conn (datomic.core/connect!)
        id (cond (p-helper/is-uuid id) (UUID/fromString id)
                 :else id)
        lookup-ref [:transition/id id]]
    (d/transact conn
                [[:db/retractEntity lookup-ref]])))

(s/defn find-one :- (s/maybe models.transition/TransitionDef)
  [id :- s/Uuid]
  (->> id
       (dh.entity/find-by-id (datomic.core/connect!) :transition/id)))

(s/defn find-all :- [models.transition/TransitionDef]
  []
  (->> :transition/id
       (dh.entity/find-all (datomic.core/connect!))))
