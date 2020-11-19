export interface IBadge {
  badge_group: string
  badge_id: string
  badge_name: string
  badge_order: string
  badge_type: 'O' | 'R'
  hover_text: string
  how_to_earn: string
  image: string
  is_new: number
  progress: number
  received_count: number
  threshold: number
}

export interface IBadgeRecent extends IBadge {
  first_received_date?: string
  last_received_date?: string
  message?: string
}

export interface IBadgeResponse {
  canEarn: IBadge[]
  closeToEarning: IBadge[]
  earned: IBadgeRecent[]
  lastUpdatedDate: string
  recent: IBadgeRecent[]
  totalPoints: [
    {
      collaborative_points: number;
      learning_points: number;
    }
  ]
}

export interface IGamificationBdageResponse {
  'Comments': IGamificationBdage[],
  'Forum Posts': IGamificationBdage[],
  'Content': IGamificationBdage[],
  'Certifications': IGamificationBdage[],
  'Quizzes': IGamificationBdage[],
  'Peer Sharing': IGamificationBdage[]
}

export interface IGamificationBdage {
  FirstName?: string
  BadgeImage?: string
  BadgeCode: string
  DateOfwinning: Date
  BadgeName: string
  BadgeCategory: string
  Description: string
  ApplicationName?: string
  BadgeCount: number
  appId?: number
  TokenNo?: number
  BadgeId: number
  BasePath: string
  AfterCompletionCriteria: string
  CongratsMessage: string
  currentCount: number
  requiredCount: number
  activityId: number
  BadgeImagePath: string
}
