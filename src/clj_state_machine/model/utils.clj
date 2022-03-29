(ns clj-state-machine.model.utils
  (:import (java.util UUID)))

(defn uuid
  []
  (UUID/randomUUID))

(defn uuid-as-string
  [uuid]
  (.toString uuid))
