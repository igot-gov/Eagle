export namespace NSDiscussData {
  export interface IDiscussJsonData {
    tabs: DiscussionTab[]
  }

  export interface DiscussionTab {
    name: string
    key: string
    badges: {
      enabled: boolean
      uri?: string
    }
    enabled: boolean
    routerLink: string
  }

  export interface IDiscussionData {
    cid: number
    tid: number
    uid: number
    mainPid: number
    slug: string
    title: string
    titleRaw: string
    lastposttime: number
    timestampISO: string
    lastposttimeISO: string
    timestamp: number
    upvotes: number
    downvotes: number
    viewcount: number
    postcount: number
    deleterUid: number
    deleted: boolean | number
    locked: boolean | number
    pinned: boolean | number
    category: {
      cid: number
      name: string
      slug: string
      icon: string
      backgroundImage: string | null
      imageClass: string
      bgColor: string
      color: string
      disabled: boolean | number
    }
    user: {
      uid: number
      username: string
      userslug: string
      reputation: number
      postcount: number
      picture: string | null
      signature: string | null
      banned: number
      status: string
      "icon:text": string | null
      "icon:bgColor": string | null
      banned_until_readable: string
    }
    teaser: any
    tags: ITag[]
    isOwner: boolean
    ignored: boolean
    unread: boolean
    bookmark: any
    unreplied: boolean
    icons: any[]
    index: number
    votes: number
    teaserPid: any
  }

  export interface ITag {
    value: string
    valueEscaped: string
    color: string
    bgColor: string
    score: number
  }
  export interface ICategorie {
    bgColor: string,
    cid: number,
    class?: string,
    color?: string,
    description: string,
    descriptionParsed: string,
    disabled: number,
    icon?: string,
    imageClass?: string,
    isSection: number,
    link?: string,
    name: string,
    numRecentReplies: number,
    order: number,
    parentCid: number,
    post_count: number,
    slug: string,
    topic_count: number,
    minTags: number,
    maxTags: number,
    totalPostCount: number,
    totalTopicCount: number,
    tagWhitelist: [],
    "unread-class"?: string,
    children?: any[],
    posts?: any[],
    teaser?: any
  }
}