(ns feedback-box.core-test
  (:require [feedback-box.core :as sut]
            [cljs.test :as t :include-macros true]))

(sut/send-data "/endpoint")
