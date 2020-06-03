import {
  IChipColor,
  IGrandChildData,
  INodeData,
  ICdpTopicChild,
  ITile,
  ILpHomeTile,
} from '../../career-development-path/models/career-development-path.model'

export const navigatorCardData = [
  {
    cardTitle: 'Career Development Path',
    cardDescription: 'Explore learning paths for you career stream',
    cardImageUrl: '/assets/common/learning-path/CDP-Img.jpg',
    routerUrl: '/learning-path/navigator/cdp',
  },
  {
    cardTitle: 'Dynamic Learning Path',
    cardDescription: 'Explore learning paths for cross functional topics',
    cardImageUrl: '/assets/common/learning-path/DLP-Img.jpg',
    routerUrl: '/learning-path/navigator/dlp/sales',
  },
]

export const childrenData: { [key: string]: IChipColor[] } = {
  Sales: [
    {
      name: 'Own your growth in Sales & KAM',
      selectedColor: 'primary',
      color: 'transparent',
    },
    {
      name: 'Sales Learning',
      selectedColor: 'primary',
      color: 'transparent',
    },
    {
      name: 'Digital Learning for Sales',
      selectedColor: 'primary',
      color: 'transparent',
    },
    {
      name: 'SieSales',
      selectedColor: 'primary',
      color: 'transparent',
    },
  ],
  'Key Account Management': [
    {
      name: 'General Overview',
      selectedColor: 'primary',
      color: 'transparent',
    },
    {
      name: 'Regional Account Manager',
      selectedColor: 'primary',
      color: 'transparent',
    },
    {
      name: 'Corporate Account Manager',
      selectedColor: 'primary',
      color: 'transparent',
    },
    {
      name: 'Global Account Manager',
      selectedColor: 'primary',
      color: 'transparent',
    },
  ],
}

export const grandChildrenData: { [key: string]: IGrandChildData[] } = {
  'Own your growth in Sales & KAM': [
    {
      name: 'Selling Methods/Approaches',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role as Key Account Manager ',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role in Sales',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Competencies',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Shortcuts/Quick Links',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
  ],
  'Sales Learning': [
    {
      name: 'Selling Methods/Approaches',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role as Key Account Manager ',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role in Sales',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Competencies',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Shortcuts/Quick Links',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
  ],
  'Digital Learning for Sales': [
    {
      name: 'Selling Methods/Approaches',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role as Key Account Manager ',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role in Sales',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Competencies',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Shortcuts/Quick Links',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
  ],
  SieSales: [
    {
      name: 'Selling Methods/Approaches',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role as Key Account Manager ',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role in Sales',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Competencies',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Shortcuts/Quick Links',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
  ],
  'General Overview': [
    {
      name: 'Selling Methods/Approaches',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role as Key Account Manager ',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role in Sales',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Competencies',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Shortcuts/Quick Links',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
  ],
  'Regional Account Manager': [
    {
      name: 'Selling Methods/Approaches',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role as Key Account Manager ',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role in Sales',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Competencies',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Shortcuts/Quick Links',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
  ],
  'Corporate Account Manager': [
    {
      name: 'Selling Methods/Approaches',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role as Key Account Manager ',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role in Sales',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Competencies',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Shortcuts/Quick Links',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
  ],
  'Global Account Manager': [
    {
      name: 'Selling Methods/Approaches',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role as Key Account Manager ',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'My Role in Sales',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Competencies',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
    {
      name: 'Shortcuts/Quick Links',
      description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
    },
  ],
}

export const nodeLevelData: { [key: string]: { [key: string]: INodeData[] } } = {
  'Selling Methods/Approaches': {
    'Value Selling': [
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
    'Consultative Selling': [
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
    'Customer Value Co-Creation': [
      {
        name: ' Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
    'Technical Selling': [
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
  },
  'My Role as Key Account Manager': {
    'Corporate Account Manager': [
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
    'Global Account Manager': [
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
    'Regional Account Manager': [
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
    Certification: [
      {
        name: 'OCA',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Siemens Sales Excellence',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
  },
  'My Role in Sales': {
    'General Sales (SL)': [
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
    ],
    'Sales Consulting': [
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
    'Sales Support and Administration (SA)': [
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'videocam',
        description: 'How to use social media to create and find new leads',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
    ],
  },
  Competencies: {
    'Interpersonal & Personal': [
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
    'Functions & Methods': [
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
  },
  'Shortcuts/Quick Links': {
    'Global Learning Campus': [
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
    'Sie Sales Job Profile Platform': [
      {
        name: 'Role description',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Trainings (M1 to M4)',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Communication with impact: Story telling',
        type: 'videocam',
        description:
          'How can you make you make your presentation more engaging? Storytelling is a way to do so!',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Food-for-Thought: Lieber die Besten für kurze Zeit',
        type: 'web',
        description: 'Mitarbeiterbindung mal hinterfragt',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
      {
        name: 'Coach and Empower your people',
        type: 'videocam',
        description: 'Coaching is one of the key elements of becoming a new leader',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'The Challenger Sale',
        type: 'videocam',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/CDP-Img.jpg',
      },
      {
        name: 'Social Selling Masterclass: An example of the LinkedIn Sales Navigator',
        type: 'web',
        description: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, seddiam',
        imageUrl: '/assets/common/learning-path/DLP-Img.jpg',
      },
    ],
  },
}

export const latestDataCDP: { [key: string]: ILpHomeTile[] } = {
  tileData1: [
    {
      titleKey: 'lex_auth_01285522270547148814',
      displayName: 'Digitalization',
      cols: 2,
      rows: 1,
      imageUrl: '/assets/common/learning-path/14.png',
      width: '270px',
      type: 'bubble',
      opacity: 1,
      greyScale: 'grayscale(0%)',
    },
    {
      titleKey: 'lex_auth_01285522239022694413',
      displayName: 'Leadership',
      cols: 2,
      rows: 1,
      imageUrl: '/assets/common/learning-path/team-with-flipchart_iStock-590298394_1-col-entry.jpg',
      width: '270px',
      type: 'tile',
      opacity: 1,
      greyScale: 'grayscale(0%)',
    },
  ],
  tileData2: [
    {
      titleKey: 'lex_auth_0128552176426270725',
      displayName: 'Sales',
      cols: 4,
      rows: 1,
      imageUrl: '/assets/common/learning-path/woman-men-handshake_iStock-973715182_card.jpg',
      width: '270px',
      type: 'bubble',
      opacity: 1,
      greyScale: 'grayscale(0%)',
    },
  ],
  tileData3: [
    {
      titleKey: 'lex_auth_01285522195622297616',
      displayName: 'Key Account Management',
      cols: 2,
      rows: 1,
      imageUrl: '/assets/common/learning-path/discussion-printing_iStock-1014150608_card.jpg',
      width: '270px',
      type: 'tile',
      opacity: 1,
      greyScale: 'grayscale(0%)',
    },
    {
      titleKey: 'lex_auth_01285522520727552015',
      displayName: 'New Work',
      cols: 2,
      rows: 1,
      imageUrl: '/assets/common/learning-path/IT-man-with-laptop_iStock-973715860_card.jpg',
      width: '270px',
      type: 'bubble',
      opacity: 0.6,
      greyScale: 'grayscale(0%)',
    },
  ],
}
export const homeNewData: { [key: string]: ITile[] } = {
  qualityMggmt: [],
  lex_auth_01285522239022694413: [
    {
      title: 'mom',
      displayName: 'Manager of Managers',
      cols: 2,
      rows: 2,
      imageUrl: '/assets/common/learning-path/sketch-on-tablet_iStock-1141040803_1-col-entry.jpg',
      width: '100%',
    },
    {
      title: 'mot',
      displayName: 'Manager of Teams',
      cols: 2,
      rows: 2,
      imageUrl: '/assets/common/learning-path/IT-team_iStock-984117566_card.jpg',
      width: '100%',
    },
    {
      title: 'fm',
      displayName: 'Future Managers',
      cols: 2,
      rows: 2,
      imageUrl:
        '/assets/common/learning-path/laptop-social-media-symbols_iStock-1059649304_1-col-entry.jpg',
      width: '100%',
    },
    {
      title: 'professional',
      displayName: 'Professionals',
      cols: 2,
      rows: 2,
      imageUrl:
        '/assets/common/learning-path/people-with-speech-bubbles_iStock-862201618_1-col-entry.jpg',
      width: '100%',
    },
  ],
  sales: [],
  lex_auth_01285522195622297616: [
    {
      title: 'general',
      displayName: 'General Overview',
      cols: 2,
      rows: 2,
      imageUrl: '/assets/common/learning-path/sketch-on-tablet_iStock-1141040803_1-col-entry.jpg',
      width: '100%',
    },
    {
      title: 'regional',
      displayName: 'Regional Account Manager',
      cols: 2,
      rows: 2,
      imageUrl: '/assets/common/learning-path/IT-team_iStock-984117566_card.jpg',
      width: '100%',
    },
    {
      title: 'corporate',
      displayName: 'Corporate Account Manager',
      cols: 2,
      rows: 2,
      imageUrl:
        '/assets/common/learning-path/laptop-social-media-symbols_iStock-1059649304_1-col-entry.jpg',
      width: '100%',
    },
    {
      title: 'global',
      displayName: 'Global Account Manager',
      cols: 2,
      rows: 2,
      imageUrl:
        '/assets/common/learning-path/people-with-speech-bubbles_iStock-862201618_1-col-entry.jpg',
      width: '100%',
    },
  ],
  newWork: [],
}
export const landingData: { [key: string]: { [key: string]: ICdpTopicChild[] } } = {
  lex_auth_01285522195622297616: {
    corporate: [
      {
        title: 'Carrier Path',
        children: [
          {
            title: 'Information Corporate Account Manager',
            children: [],
          },
          {
            title: 'Nomination',
            children: [
              {
                title: 'Information for the Nomination Process',
                img: '/assets/common/learning-path/DLP-Img.jpg',
              },
              {
                title: 'Template Nomination Letter CAM',
                img: '/assets/common/learning-path/CDP-Img.jpg',
              },
            ],
            progress: 35,
            img: '/assets/common/learning-path/DLP-Img.jpg',
          },
          {
            title: 'Online Competence Analysis (OCA)',
            children: [
              {
                title: 'Information OC',
                children: [],
              },
            ],
            progress: 50,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'KAM Excellence Training M1: Creating Value Based Relationships',
            children: [
              {
                title: 'Information KAM Excellence Training M1: Creating Value Based Relationships',
                img: '/assets/common/learning-path/DLP-Img.jpg',
                children: [],
                progress: 90,
              },
              {
                title:
                  'Learning Path KAM Excellence Training M1: Creating Value Based Relationships',
                img: '/assets/common/learning-path/CDP-Img.jpg',
                children: [],
                progress: 90,
              },
              {
                title: 'Portal registration',
                img: '/assets/common/learning-path/DLP-Img.jpg',
                children: [],
                progress: 90,
              },
              {
                title: `WBT's`,
                img: '/assets/common/learning-path/CDP-Img.jpg',
                children: [
                  {
                    title: 'How to sell an Opportunity Internally',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Executive Discovery',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Effective Positioning Statement',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Customer Specific Value Proposition',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Executive Whiteboard Presentations',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Relationship Matrix',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'SWOT Analysis',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Positioning Customer Specific Value',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Building and Leading Global Teams',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Key Customer Decision Maker',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411764275281922077',
                            'lex_auth_0128411699361955841976',
                            'lex_auth_0128411734670950402083',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 45,
              },
            ],
          },
          {
            title: 'KAM Excellence Training M4: Mastering Influencing Skills',
            children: [
              {
                title: 'Information KAM Excellence Training M4: Mastering Influencing Skills',
                children: [],
              },
              {
                title: 'Learning Path KAM Excellence Training M4: Mastering Influencing Skills',
                children: [],
              },
              {
                title: 'Portal registratio',
                children: [],
              },
              {
                title: 'WBT´s',
                children: [
                  {
                    title: 'Human Information Processing (HIP) and influencing outcomes',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'The Professional Relationship',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Key Principles of Influence',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'The Influencing Power of Language',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Personal Social Brand and Thought Leadership',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Forming insightful questions that inspire confidence in others',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Personality Styles and Behavior',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Formulating the Commercial Story',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Listening to others and hearing beyond words',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Body stance and body language for presentations',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                ],
                progress: 65,
              },
            ],
          },
          {
            title: 'Knowledge Check',
            children: [
              {
                title: 'Information for the Knowledge Check',
                children: [],
              },
            ],
          },
          {
            title: 'KAM Cert OCA',
            children: [
              {
                title: 'Information for the  Certification Online Competence Analysis',
                children: [],
              },
            ],
          },
          {
            title: 'KAM Review',
            children: [
              {
                title: 'Information KAM Review',
                children: [],
              },
            ],
          },
        ],
        progress: 85,
      },
    ],
    regional: [
      {
        title: 'regional',
        children: [
          {
            title: 'Nomination',
            children: [
              {
                title: 'Information for the Nomination Process ',
                children: [],
              },
            ],
            progress: 45,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'Online Competence Analysis (OCA)',
            children: [
              {
                title: 'Template Nomination Letter RAM',
                children: [],
              },
            ],
            progress: 50,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'KAM Excellence Training M1: Creating Value Based Relationships',
            children: [
              {
                title: 'Information KAM Excellence Training M1: Creating Value Based Relationships',
                children: [],
              },
              {
                title:
                  'Learning Path KAM Excellence Training M1: Creating Value Based Relationships',
                children: [],
              },
              {
                title: 'Portal registration',
                children: [],
              },
              {
                title: 'WBT´s',
                children: [
                  {
                    title: 'How to sell an Opportunity Internally',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Executive Discovery',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Effective Positioning Statement',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Customer Specific Value Proposition',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Executive Whiteboard Presentations',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Relationship Matrix',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'SWOT Analysis',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Positioning Customer Specific Value',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Building and Leading Global Teams',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Key Customer Decision Maker',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                ],
                progress: 76,
              },
            ],
            progress: 50,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'KAM Excellence Training M4: Mastering Influencing Skills',
            children: [
              {
                title: 'Information KAM Excellence Training M4: Mastering Influencing Skills',
                children: [],
              },
              {
                title: 'Learning Path KAM Excellence Training M4: Mastering Influencing Skills',
                children: [],
              },
              {
                title: 'Portal registration',
                children: [],
              },
              {
                title: `WBT's`,
                img: '/assets/common/learning-path/CDP-Img.jpg',
                children: [
                  {
                    title: 'Human Information Processing (HIP) and influencing',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'The Professional Relationship',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Key Principles of Influence',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'The Influencing Power of Language',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Human Information Processing (HIP) and influencing',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Personal Social Brand and Thought Leadership',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Forming insightful questions that inspire confidence in others',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Personality Styles and Behavior',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Formulating the Commercial Story',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Listening to others and hearing beyond words',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Body stance and body language for presentatio',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                progress: 65,
              },
            ],
            progress: 50,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'Knowledge Check',
            children: [
              {
                title: 'Information for the Knowledge Check',
                children: [],
              },
            ],
          },
          {
            title: 'KAM Cert OCA',
            children: [
              {
                title: 'Information for the  Certification Online Competence Analysis',
                children: [],
              },
            ],
          },
          {
            title: 'KAM Review',
            children: [
              {
                title: 'Information KAM Review',
                children: [],
              },
            ],
          },
        ],
        progress: 50,
      },
    ],
    general: [
      {
        title: 'general',
        children: [
          {
            title: 'Sales Excellence',
            children: [],
            progress: 45,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'Information',
            children: [],
            progress: 50,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
        ],
        progress: 69,
      },
    ],
    global: [
      {
        title: 'global',
        children: [
          {
            title: 'Nomination',
            children: [
              {
                title: 'Information for the Nomination Process ',
                children: [],
              },
            ],
            progress: 45,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'Online Competence Analysis (OCA)',
            children: [
              {
                title: 'Template Nomination Letter RAM',
                children: [],
              },
            ],
            progress: 50,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'KAM Excellence Training M1: Creating Value Based Relationships',
            children: [
              {
                title: 'Information KAM Excellence Training M1: Creating Value Based Relationships',
                children: [],
              },
              {
                title:
                  'Learning Path KAM Excellence Training M1: Creating Value Based Relationships',
                children: [],
              },
              {
                title: 'Portal registration',
                children: [],
              },
              {
                title: 'WBT´s',
                children: [
                  {
                    title: 'How to sell an Opportunity Internally',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Executive Discovery',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Effective Positioning Statement',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Customer Specific Value Proposition',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Executive Whiteboard Presentations',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Relationship Matrix',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'SWOT Analysis',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Positioning Customer Specific Value',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Building and Leading Global Teams',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Key Customer Decision Maker',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: ['lex_auth_0128411716321607681979'],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                } ,
              },
            ],
            progress: 50,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'KAM Excellence Training M4: Mastering Influencing Skills',
            children: [
              {
                title: 'Information KAM Excellence Training M4: Mastering Influencing Skills',
                children: [],
              },
              {
                title: 'Learning Path KAM Excellence Training M4: Mastering Influencing Skills',
                children: [],
              },
              {
                title: 'Portal registration',
                children: [],
              },
              {
                title: `WBT's`,
                img: '/assets/common/learning-path/CDP-Img.jpg',
                children: [
                  {
                    title: 'Generating and Identifying Opportunities',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Qualifying Opportunities and Developing the Opportunity Strategy',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Team to Team Alignment and Leveraging Relationships',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Pipeline Management and Prioritizing Opportunities',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                  {
                    title: 'Converting Opportunities into Wins',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                progress: 65,
              },
            ],
            progress: 50,
            img: '/assets/common/learning-path/CDP-Img.jpg',
          },
          {
            title: 'Knowledge Check',
            children: [
              {
                title: 'Information for the Knowledge Check',
                children: [],
              },
            ],
          },
          {
            title: 'KAM Cert OCA',
            children: [
              {
                title: 'Information for the  Certification Online Competence Analysis',
                children: [],
              },
            ],
          },
          {
            title: 'KAM Review',
            children: [
              {
                title: 'Information KAM Review',
                children: [],
              },
            ],
          },
        ],
        progress: 80,
      },
    ],
  },
  lex_auth_01285522239022694413: {
    mom: [
      {
        title: 'Carrier Path',
        children: [
          {
            title: 'Develop your Business',
            children: [
              {
                title: 'Entrepreneurial thinking',
                children: [
                  {
                    title: 'Taking a new leadership role',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411789895761922550',
                            'lex_auth_01284541727349145619274',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 56,
              },
              {
                title: 'Developing self awareness and emotional intelligence',
                children: [
                  {
                    title: 'Is it possible to run a company and reinvent it at the same time?',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411762459361282638',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 46,
              },
            ],
          },
          {
            title: 'Manage and enable performance of other',
            children: [
              {
                title: 'Building and leading efffective teams',
                children: [
                  {
                    title: 'Wie verändert die Digitalisierung Arbeit und Führung?',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'People and Business Management Learning Program 2 (PBM2)',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411759199682562513',
                            'lex_auth_0128411769426903042146',
                            'lex_auth_0128411662183874561565',
                            'lex_auth_012841108950663168377',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 36,
              },
              {
                title: 'Coaching & Developing Others',
                children: [
                  {
                    title: 'Coaching GROW Modell',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411793271521282518',
                            'lex_auth_0128411778900295682519',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 96,
              },
            ],
          },
        ],
        progress: 66,
      },
    ],
    mot: [
      {
        title: 'Carrier Path',
        children: [
          {
            title: 'Develop your business',
            children: [
              {
                title: 'Developing and driving strategy',
                children: [
                  {
                    title: '4 ways to improve your strategic thinking skills',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'The Role of Strategic Thinking in Business Planning',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411787466833922608',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 86,
              },
              {
                title: 'Driving innovation and change',
                children: [
                  {
                    title: 'Culture First! Learning from the pioneers of the digital revolution',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411780602839042554',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 76,
              },
              {
                title: 'Driving a customer centric culture',
                children: [
                  {
                    title: 'Culture eats strategy for breakfast',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411760833986562558',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 66,
              },
              {
                title: 'Leveraging Business skills and Knowledge',
                children: [
                  {
                    title: 'People and Business Management Learning Program 1 (PBM1)',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411735244636162148',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 56,
              },
            ],
          },
          {
            title: 'Manage and enable performance of other',
            children: [
              {
                title: 'Leading with vision and purpose',
                children: [
                  {
                    title: 'What is the purpose of your business?',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'Inspiring Leadership in the Digital Age',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411775754813442555',
                            'lex_auth_0128411808100270082512',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 46,
              },
              {
                title: 'Coaching & Developing Others',
                children: [
                  {
                    title: 'Coaching GROW Modell',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411793271521282518',
                            'lex_auth_0128411778900295682519',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 36,
              },
            ],
          },
        ],
        progress: 77,
      },
    ],
    fm: [
      {
        title: 'Carrier Path',
        children: [
          {
            title: 'Develop yourself',
            children: [
              {
                title: 'Willingness to learn',
                children: [
                  {
                    title: 'Taking a new leadership role',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128454172734914561927',
                            'lex_auth_0128411789895761922550',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 96,
              },
              {
                title: 'Developing self awareness and emotional intelligence',
                children: [
                  {
                    title: 'Your first steps as a manager: a few golden rules',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                  {
                    title: 'People and Business Management Learning Program 3 (PBM3)',
                    img: '/assets/common/learning-path/CDP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_012845352502640640176',
                            'lex_auth_012845348924678144116',
                            'lex_auth_012845351237844992175',
                            'lex_auth_012845355865776128114',
                            'lex_auth_0128411818938368002553',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 86,
              },
            ],
          },
          {
            title: 'Manage and enable performance of other',
            children: [
              {
                title: 'Building and leading efffective teams',
                children: [
                  {
                    title: 'Leadership checklists (first 100 days)',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411826906808322559',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 76,
              },
              {
                title: 'Coaching & Developing Others',
                children: [
                  {
                    title: 'Coaching GROW Modell',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128411793271521282518',
                            'lex_auth_0128411778900295682519',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 66,
              },
            ],
          },
        ],
        progress: 88,
      },
    ],
    professional: [
      {
        title: 'Carrier Path',
        children: [
          {
            title: 'Develop yourself',
            children: [
              {
                title: 'Willingness to learn',
                children: [
                  {
                    title: 'Accept your mistakes',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128453921984102407955',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 56,
              },
            ],
          },
          {
            title: 'Manage and enable performance of other',
            children: [
              {
                title: 'Leading with vision and purpose',
                children: [
                  {
                    title: 'What is leadership',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                progress: 56,
              },
              {
                title: 'Giving and receiving feedback',
                children: [
                  {
                    title: 'Ask for feedback',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128453906264227847945',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 46,
              },
            ],
          },
          {
            title: 'Management Essentials',
            children: [
              {
                title: 'Promote "Own your career" philosophy',
                children: [
                  {
                    title: 'Motivate people on an individual basis',
                    img: '/assets/common/learning-path/DLP-Img.jpg',
                  },
                ],
                strips: {
                  widgetData: {
                    strips: [
                      {
                        key: 'CDP',
                        title: '',
                        request: {
                          ids: [
                            'lex_auth_0128453884243066887255',
                          ],
                        },
                      },
                    ],
                  },
                  widgetSubType: 'contentStripMultiple',
                  widgetType: 'contentStrip',
                  widgetHostClass: '',
                },
                progress: 36,
              },
            ],
          },
        ],
        progress: 100,
      },
    ],
  },
}
export const cdpWidgetData: { [key: string]: string[] } = {
  'Development and driving strategy': ['lex_auth_0128411787466833922608'],
}
