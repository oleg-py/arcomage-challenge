var path = require("path");
var CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  mode: "development",
  output: {
    "filename": "[name]-bundle.js"
  },
  resolve: {
    alias: {
      "resources": path.resolve(__dirname, "../../../../src/main/resources")
    }
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [ 'style-loader', 'css-loader' ]
      },

      {
        test: /\.styl$/,
        use: [ 'style-loader', 'css-loader', 'stylus-loader' ]
      },
      // "file" loader for png
      {
        test: /\.(png|jpe?g)$/,
        use: [
          {
            loader: 'file-loader',
            query: {
              name: 'static/media/[name].[hash:8].[ext]'
            }
          }
        ]
      },
      {
          test: /\.csv$/,
          loader: 'csv-loader',
          options: {
              dynamicTyping: true,
              header: true,
              skipEmptyLines: true
          }
      }
    ]
  },
  plugins: [
    new CopyWebpackPlugin([
      { from: path.resolve(__dirname, "../../../../public") }
    ])
  ],
  devServer: {
    host: "0.0.0.0"
  }
}
