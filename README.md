# michou

**/!\ Note: michou is WIP**

> "When I need something, I just ask michou."
> -- fred

Michou is a little tool to configure your dotfiles from a structured directory.

With michou, you can organize your dotfiles backups the way you want,
and you describe how it should be deployed on your system with a small
declarative language.

## Quick overview

Here's an example of how you would describe the deployment of some dotfiles
in michou's language:

```
layer base:
  into ~ :
    .vimrc
    .tmux.conf <- tmux.conf

    into .clojure:
      deps.edn <- clojure/deps.edn

    into .config/badger:
      from badger:
        badgerc
        badger.prod <- badger.template
```

## Installation

WIP


## Usage

WIP

Run the project directly (development mode):

    $ clj -A:run /path/to/michou/script

Run the tests:

    $ clj -A:test:runner

Or for detecting changes and re-run automatically:

	$ clj -A:test:runner --watch
