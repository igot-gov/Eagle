import { Router } from 'express'
const fs = require('fs')

export const scoringTemplateApi = Router()

scoringTemplateApi.get('/getTemplate/content_scoring_template', async (_req, res) => {
  try {
    // tslint:disable-next-line: no-identical-functions
    fs.readFile(__dirname + '/../static-data/scoringtemplate.json', (err: Error, json: string) => {
      if (!err) {
        const obj = JSON.parse(json)
        res.json(obj)
      }
    })
  } catch (err) {
    res.status((err && err.response && err.response.status) || 500).send(err)
  }
})
