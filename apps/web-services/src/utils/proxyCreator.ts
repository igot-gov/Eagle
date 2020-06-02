import { Router } from 'express'
import { createProxyServer } from 'http-proxy'

const proxyCreator = (timeout = 10000) => createProxyServer({
  timeout,
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
