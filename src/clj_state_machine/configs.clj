(ns clj-state-machine.configs
  (:require [outpace.config :refer [defconfig]]))

(defconfig env)

(defn env-test?
  []
  (= "test" env))

(def cookies
  {:language "csm_language"})

;TODO implement cookies logic
(defn get-language
  [_]
  (let [default :en]
    default))
