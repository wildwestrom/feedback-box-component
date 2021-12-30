(ns feedback-box.core
  (:require [ajax.core :refer [POST]]
            [garden.core :refer [css]]
            [garden.selectors :as selector]
            [rum.core :as rum]))

(def styles
  (css
   [:#warning {:background :yellow
               :font-weight :bold
               :font-size "1.5rem"
               :border-width "1px"
               :border-style :solid
               :border-radius ".375rem"
               :padding ".25rem"}
    [(selector/& selector/before) {:content "\"⚠️\""
                                   :padding-right ".375rem"}]]
   [:.greeting {:font-size "1.5rem"}
    [(selector/& selector/before) {:content "\"📣\""
                                   :padding-right ".375rem"}]]
   [:.form-container {:display :flex
                      :flex-direction :column
                      :text-align :center
                      :max-width "60ch"
                      :gap ".5em"}
    [:textarea {:resize :vertical
                :min-height "2rem"}]
    [:span.form-field {:display :flex
                       :gap ".5rem"}
     [:input {:flex-grow 1}]]]))

(defn send-data [endpoint]
  (let [form-data (js/FormData.
                   (js/document.getElementById "feedback-form"))]
    (POST endpoint
      {:body form-data})))

(rum/defc feedback-box
  [endpoint & options]
  (let [{:keys [greeting
                feedback-placeholder
                email-placeholder]}
        ;; I'd like to destructure using `:or`, but unfortunately,
        ;; The function that calls this component will send the keys
        ;; regardless of whether they're `nil` or not.
        options]
    [:<>
     [:style styles]
     [:form.form-container#feedback-form
      {:on-submit #(do (.preventDefault %)
                       (send-data endpoint))}
      (when-not endpoint
        (let [warning-msg "Warning: No endpoint set!"]
          (js/console.log warning-msg)
          [:span#warning warning-msg]))
      [:span.greeting (or greeting
                          "Please send us feedback.")]
      [:textarea
       {:id "feedback-input"
        :form "feedback-form"
        :rows 5
        :placeholder (or feedback-placeholder
                         "Type feedback here.")
        :required true}]
      [:span.form-field
       [:label {:for :email} "Email:"]
       [:input {:id "email-input"
                :form "feedback-form"
                :type :email
                :placeholder (or email-placeholder
                                 "user@example.com")
                :required true}]]
      [:input {:type :submit
               :required true}]]]))

(defn start []
   ;; start is called by init and after code reloading finishes
   ;; this is controlled by the :after-load in the config
  (let [div-to-mount (js/document.getElementById "feedbackbutton")
        get-attr (fn [attrname] (.getAttribute div-to-mount attrname))
        endpoint (get-attr "endpoint")
        greeting (get-attr "greeting")
        feedback-placeholder (get-attr "feedback-placeholder")
        email-placeholder (get-attr "email-placeholder")]
    (rum/mount (feedback-box endpoint
                             :greeting greeting
                             :feedback-placeholder feedback-placeholder
                             :email-placeholder email-placeholder)
               div-to-mount)))

(defn ^:export init []
   ;; init is called ONCE when the page loads
   ;; this is called in the index.html and must be exported
   ;; so it is available even in :advanced release builds
  (start))

(defn stop []
   ;; stop is called before any code is reloaded
   ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
