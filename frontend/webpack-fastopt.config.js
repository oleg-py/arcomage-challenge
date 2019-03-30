var merge = require('webpack-merge');
var core = require('./webpack-core.config.js')
var path = require("path");
var HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = merge(core, require('./scalajs.webpack.config'), {
  entry: {
    "dependencies": ["./frontend-fastopt-entrypoint.js"],
    "frontend-fastopt": ["./hot-launcher.js"]
  },
  output: {
    path: __dirname,
    filename: "[name]-library.js",
    library: "appLibrary",
    libraryTarget: "var"
  },
  devtool: "source-map",
  module: {
    noParse: (content) => {
      return content.endsWith("-fastopt.js");
    }
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, "../../../../public/index-fastopt.html"),
      inject: false
    })
  ]
})
