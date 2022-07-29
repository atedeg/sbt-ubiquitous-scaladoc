# [1.0.0-beta.6](https://github.com/atedeg/sbt-ubiquitous-scaladoc/compare/v1.0.0-beta.5...v1.0.0-beta.6) (2022-07-29)


### Bug Fixes

* clarify error codes when publishing ([207600c](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/207600cdc175d6d8284dc666e861eab98ea0228c))

# [1.0.0-beta.5](https://github.com/atedeg/sbt-ubiquitous-scaladoc/compare/v1.0.0-beta.4...v1.0.0-beta.5) (2022-07-29)


### Bug Fixes

* fix behaviour in case of leftover entities ([a00fead](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/a00feaddc3844c7f5c8e5e9a1976cea669cf8f4f))

# [1.0.0-beta.4](https://github.com/atedeg/sbt-ubiquitous-scaladoc/compare/v1.0.0-beta.3...v1.0.0-beta.4) (2022-07-26)


### Bug Fixes

* removed jitpack resolver for markdown generator library ([b519ce1](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/b519ce1e3979068b3958f50c1e69cfd9bef013e1))

# [1.0.0-beta.3](https://github.com/atedeg/sbt-ubiquitous-scaladoc/compare/v1.0.0-beta.2...v1.0.0-beta.3) (2022-07-20)


### Features

* no longer fail when there are ignored entities ([290327e](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/290327ee66aaebb703690bf82a89d835d721bb10))

# [1.0.0-beta.2](https://github.com/atedeg/sbt-ubiquitous-scaladoc/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2022-07-19)


### Bug Fixes

* add .yaml extension to default configuration file ([cf3b685](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/cf3b685c8c5baca8f237f654793cc502f2e0d3c3))
* create target directory if it does not exist when serializing a table ([e23c573](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/e23c57399f72e4e7ef4ac2436cabf06a422c22df))
* fix bug in file lookup ([94b0c63](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/94b0c63b01d12ef23b3bd840fb6422cfa86e9ef8))
* fix configuration parsing ([e40205e](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/e40205ea317fca59beb3a63b37ea9d70097d9b50))
* fix error message typing ([2d6ce55](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/2d6ce55736b1ab24d1da340c6e94843b64e8e8bb))
* fix json parsing of IgnoredSelectors ([9c0be26](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/9c0be26b7979930d9bd659aa9f414362b20dc7d1))
* fix resolution of selector ([a9bf2e7](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/a9bf2e7a69e8d7879955f4305aff8de4793dd1db))
* fix table serialisation ([330cec7](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/330cec71f7fb8b7a720028c49617f860cc883516))
* link generation ([4c40fff](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/4c40fff5fd8927cb1a3adeb393f7bd1567dd199d))
* link in markdown table ([4ed6db6](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/4ed6db6fccac1781fdcd49f37de724fe23996c6f))
* lookupdir instead of working dir in Ubidoc file ([5583f76](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/5583f768a86da3a6f0c1c3ac70a82fea4418a5df))
* only consider html files ([2fd5769](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/2fd5769b3479d83c11d3e351dfa75ce8ce69663e))
* table serialization only applies to Tables with Rows and not entitites ([1a95bb1](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/1a95bb16fd58ae654357100abf8747f1b1f6276f))
* world normalization ([7125224](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/7125224df93dfe52dfaca08f600b97b2e56f98ef))


### Features

* add entities parsing ([0ac73da](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/0ac73daf01d208aafceddb53a301d327baefeb4c))
* add Entity parsing ([fdb1525](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/fdb1525e38eb8b1e3194e366abb21b69c5266644))
* add multiple table reading and ignored files ([6092729](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/6092729abf2e8c8af7bac76c676d601d385e6083))
* change configuration spec to avoid connflicts ([32d02d8](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/32d02d86fd517bef59603ec4af11f79c0b3cdb83))
* configuration parsing ([5113a6b](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/5113a6bc04ec0d3a36d0bf82d20fadbbff61c7a3))
* correctly resolve documentation links ([26f4ed7](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/26f4ed7dd957edb8245b69b976c00c81cfd7e271))
* normalize row names ([88cec85](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/88cec8509ef37240020570c5fec32ce5161f6c8c))
* table generation from all entities file ([8cf1473](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/8cf1473686feea7f4dafbc81f11ceb4be9ae3e54))
* use new selector kind ([b2d4deb](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/b2d4debaa9f3f744f7582e02c57c37d25983c23f))

# 1.0.0-beta.1 (2022-07-18)


### Bug Fixes

* add configuration for import sort in scalafix ([9590e89](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/9590e891ce593284993c6054aa5632c7a922374e))
* get branch name from git command ([e0fe7e8](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/e0fe7e89ffb14d66e7b2d6d68c8e888ed0ca0d32))
* plugin parameters ([cc6aa32](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/cc6aa32090b00bef8a14cef5a4281d8c32ef85ae))
* release script ([b3c5174](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/b3c5174e36623241fe60fb3f8b5b1c3ef530e1a6))
* scalafmt reformat ([3ad85a6](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/3ad85a6607fabf5ec11467ce5bb2dcf4c90c230e))
* wartremover errors ([deb41cb](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/deb41cbb72fb839b8ef6400a176d94389ec42396))
* wartremover silence errors ([b9c3500](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/b9c35005ec83c83bf1ecb8aeb5e38cf7a0091a09))


### Features

* add fileNameSuffix key ([cb97277](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/cb97277e51258db80bb07e540072930792266725))
* add plugin keys ([b8d92cb](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/b8d92cb2c12f6476a60f73bba9a17528008b8018))
* ubiquitousScaladoc plugin implementation ([06f0210](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/06f0210c859e4affc99a02d544bef4fae56d93a4))

# [1.0.0-beta.3](https://github.com/atedeg/sbt-ubiquitous-scaladoc/compare/1.0.0-beta.2...1.0.0-beta.3) (2022-07-03)


### Bug Fixes

* release script ([b3c5174](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/b3c5174e36623241fe60fb3f8b5b1c3ef530e1a6))
* wartremover silence errors ([b9c3500](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/b9c35005ec83c83bf1ecb8aeb5e38cf7a0091a09))


### Features

* add fileNameSuffix key ([cb97277](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/cb97277e51258db80bb07e540072930792266725))

# [1.0.0-beta.2](https://github.com/atedeg/sbt-ubiquitous-scaladoc/compare/1.0.0-beta.1...1.0.0-beta.2) (2022-06-19)


### Bug Fixes

* get branch name from git command ([e0fe7e8](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/e0fe7e89ffb14d66e7b2d6d68c8e888ed0ca0d32))

# 1.0.0-beta.1 (2022-06-19)


### Bug Fixes

* add configuration for import sort in scalafix ([9590e89](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/9590e891ce593284993c6054aa5632c7a922374e))
* plugin parameters ([cc6aa32](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/cc6aa32090b00bef8a14cef5a4281d8c32ef85ae))
* scalafmt reformat ([3ad85a6](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/3ad85a6607fabf5ec11467ce5bb2dcf4c90c230e))
* wartremover errors ([deb41cb](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/deb41cbb72fb839b8ef6400a176d94389ec42396))


### Features

* add plugin keys ([b8d92cb](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/b8d92cb2c12f6476a60f73bba9a17528008b8018))
* ubiquitousScaladoc plugin implementation ([06f0210](https://github.com/atedeg/sbt-ubiquitous-scaladoc/commit/06f0210c859e4affc99a02d544bef4fae56d93a4))
