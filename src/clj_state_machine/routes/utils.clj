(ns clj-state-machine.routes.utils
  (:require [clj-state-machine.controller.utils :as c.utils]
            [clj-state-machine.config :as config]
            [clj-state-machine.i18n.translations :as i.t])
  (:use clojure.pprint))

;TODO: add custom ex handling
(defn message-catch
  [request e]
  (let [data (.getData e)
        type (get data :type :none)
        language (config/get-language request)
        fatal (:fatal (get i.t/all language))
        message (get data :message fatal)]
    (c.utils/common-with-custom-message type message)))


