// tslint:disable-next-line: no-commented-code
import { NextFunction, Request, Response, Router } from 'express'

// tslint:disable-next-line: no-commented-code
export const editorApi = Router()

editorApi.use((_req: Request, _res: Response, next: NextFunction) => {
  next()
})

// editorApi.get('/getCompleteDetails/:id', (req: Request, res: Response) => {
//   const isAdmin: boolean = req.query.isAdmin
// })
