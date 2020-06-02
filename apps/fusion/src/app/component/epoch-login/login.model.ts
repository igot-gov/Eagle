export interface IWSPublicLoginConfigEpoch {
  bodyBackgroundImageUrl: string
  footer: ILoginFooterConfig
  displayName: { [key: string]: string }
  loginButtons: ILoginButton[]
  isClient: boolean
}

export interface ILoginButton {
  name: string
  key: string
  redirectUrl?: string
}

export interface ILoginFooterConfig {
  contactUs: boolean
  copyright: boolean
  faq: boolean
  aboutUs: boolean
  hasLogo?: boolean
  mobileApps?: boolean
  isVisible?: boolean
  logoUrl?: string
  tnc: boolean
  descriptiveInfo?: { [key: string]: string }
  descriptiveFooter?: ILoginDescriptiveFooterConfig
  android?: string
  ios?: string
}

export interface ILoginDescriptiveFooterConfig {
  available: boolean
  welcomeMessage: string
  welcomeTagline: string
}
