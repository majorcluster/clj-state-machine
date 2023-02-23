(ns clj-state-machine.ports.datomic.transition
  (:require [clj-state-machine.ports.datomic.core :as datomic.core]
            [datomic-helper.entity :as dh.entity]
            [datomic.api :as d]
            [pedestal-api-helper.params-helper :as p-helper])
  (:import (java.util UUID)))

(defn upsert!
  [workflow-id entity]
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

(defn delete!
  [id]
  (let [conn (datomic.core/connect!)
        id (cond (p-helper/is-uuid id) (UUID/fromString id)
                 :else id)
        lookup-ref [:transition/id id]]
    (d/transact conn
                [[:db/retractEntity lookup-ref]])))
