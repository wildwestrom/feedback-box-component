#  Feedback Box Component

## What is it?
A .js file you can embed on any page to add a feedback form. It is built using Rum and Shadow-cljs.

## Build Instructions

```console
pnpm deps && pnpm release
```

## Usage

```html
    <div id="feedbackbutton"
         endpoint="/endpoint"
         greeting="Send us feedback please!"
         feedback-placeholder="Put your feedback here."
         email-placeholder="jsmith@example.com">
    </div>
```
