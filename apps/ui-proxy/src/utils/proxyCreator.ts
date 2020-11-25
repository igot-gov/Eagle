import { Router } from 'express'
import { createProxyServer } from 'http-proxy'
import { extractUserIdFromRequest, extractUserToken } from '../utils/requestExtract'
import { logInfo } from './logger'

const proxyCreator = (timeout = 10000) => createProxyServer({
  timeout,
})
const proxy = createProxyServer({})

// tslint:disable-next-line: no-any
proxy.on('proxyReq', (proxyReq: any, req: any, _res: any, _options: any) => {
  proxyReq.setHeader('X-Channel-Id', '0131397178949058560')
  // tslint:disable-next-line: max-line-length
  proxyReq.setHeader('Authorization', 'bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJRekw4VVA1dUtqUFdaZVpMd1ZtTFJvNHdqWTg2a2FrcSJ9.TPjV0xLacSbp3FbJ7XeqHoKFN35Rl4YHx3DZNN9pm0o')
  proxyReq.setHeader('x-authenticated-user-token', extractUserToken(req))
  // tslint:disable-next-line: no-console
  console.log('proxyReq.headers:', proxyReq.header)
  if (req.body) {
    var bodyData = JSON.stringify(req.body);
    proxyReq.setHeader('Content-Length', Buffer.byteLength(bodyData))
    proxyReq.write(bodyData)
    }
})

export function proxyCreatorRoute(route: Router, targetUrl: string, timeout = 10000): Router {
  route.all('/*', (req, res) => {
    const downloadKeyword = '/download/'
    if (req.url.startsWith(downloadKeyword)) {
      req.url = downloadKeyword + req.url.split(downloadKeyword)[1].replace(/\//g, '%2F')
    }
    // tslint:disable-next-line: no-console
    console.log('REQ_URL_ORIGINAL', req.originalUrl)
    // tslint:disable-next-line: no-console
    console.log('REQ_URL', req.url)
    proxyCreator(timeout).web(req, res, {
      target: targetUrl,
    })
  })
  return route
}

export function ilpProxyCreatorRoute(route: Router, baseUrl: string): Router {
  route.all('/*', (req, res) => {
    proxyCreator().web(req, res, {
      headers: { ...req.headers } as { [s: string]: string },
      target: baseUrl + req.url,
    })
  })
  return route
}

export function scormProxyCreatorRoute(route: Router, baseUrl: string): Router {
  route.all('/*', (req, res) => {
    proxyCreator().web(req, res, {
      target: baseUrl,
    })
  })
  return route
}

export function proxyCreatorSunbird(route: Router, targetUrl: string, _timeout = 10000): Router {
  route.all('/*', (req, res) => {
    logInfo('proxyCreatorSunbird ---')
    // tslint:disable-next-line: no-console
    console.log('req headers', req.headers)
    // tslint:disable-next-line: no-console
    console.log('req header', req.header)
    // tslint:disable-next-line: no-console
    console.log('REQ_URL_ORIGINAL', req.originalUrl)
    // tslint:disable-next-line: no-console
    console.log('REQ_URL', req.url)
    proxy.web(req, res, {
      changeOrigin: true,
      ignorePath: true,
      target: targetUrl,
    })
  })
  return route
}

export function proxyCreatorToAppentUserId(route: Router, targetUrl: string, _timeout = 10000): Router {
  route.all('/*', (req, res) => {
    const userId = extractUserIdFromRequest(req).split(':')
    logInfo('proxyCreatorSunbird ---')
    // tslint:disable-next-line: no-console
    console.log('req headers', req.headers)
    // tslint:disable-next-line: no-console
    console.log('req header', req.header)
    // tslint:disable-next-line: no-console
    console.log('REQ_URL_ORIGINAL', req.originalUrl)
    // tslint:disable-next-line: no-console
    console.log('REQ_URL', req.url)
    proxy.web(req, res, {
      changeOrigin: true,
      ignorePath: true,
      target: targetUrl + userId[userId.length - 1],
    })
  })
  return route
}
