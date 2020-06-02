export const contentTypesList = ['Resource', 'Course', 'Knowledge Board', 'Knowledge Artifact']

export const dropDownOptions = {
  visibility: [{ value: 'Private', text: 'Don\'t Allow' }, { value: 'Public', text: 'Allow' }],
  exclusiveContent: [{ value: 'true', text: 'Paid' }, { value: 'false', text: 'Free' }],
  idealScreenSize: [{ value: '7', text: 'Mobile' }, { value: '10', text: 'Tablet' }, { value: '13', text: 'Desktop' }],
  // contentType: [{value: }]
  //    ''
}

export const contentTypeList: IConditionValue<string>[] = [{
  conditions: {
    fit: ['*'] as any,
  },
  value: 'Resource',
},
{
  conditions: {
    fit: ['*'],
  },
  value: 'Course',
},
{
  conditions: {
    fit: [{ contentType: ['Knowledge Board'] }],
  },
  value: 'Knowledge Board',
},
{
  conditions: {
    fit: [{ contentType: ['Knowledge Artifact'] }],
  },
  value: 'Knowledge Artifact',
},
]

import { IMetaTextUnit, IMetaTextAreaUnit, IMetaUnit, IMetaDropDownUnit, IConditionValue, IMetaNumberUnit } from '../../../../interface/meta'

export const dropDown: IMetaUnit<IMetaDropDownUnit> = {
  name: 'sourceName',
  defaultValue: [
    {
      conditions: { fit: [{ contentType: ['Resource'] }] },
      value: 'My Learning World',
    }
    ,
    {
      conditions: { fit: [{ contentType: ['Knowledge Board"'] }] },
      value: 'My Learning World',
    },
  ] as any,
  placeHolder: [],
  format: 'string',
  info: [
    { conditions: {}, value: '' },
  ],
  displayName: [],
  isMandatory: [
    { fit: ['*'] } as any,
  ],
  isDisabled: [],
  canCascade: false,
  canShow: [
    { fit: ['*'] } as any,
  ],
  meta: {
    inputType: 'textDropdown',
    validations: {
      minLength: [],
      maxLength: [],
    },
    isMultiple: false,
    displayValue: '',
    storedValue: '',
    ordinalsName: 'sourceName',

  } as any,

}

export const textAreaSample: IMetaUnit<IMetaTextAreaUnit> = {
  name: 'description',
  defaultValue: [],
  placeHolder: [],
  format: 'string',
  info: [
    { conditions: {}, value: '' },
  ],
  displayName: [
    { conditions: { fit: ['*'], notFit: [{ org: ['Infosys Ltd'] }] } as any, value: 'title' },
  ],
  isMandatory: [
    { fit: ['*'] } as any,
  ],
  isDisabled: [],
  canCascade: false,
  canShow: [
    { fit: ['*'] } as any,
  ],
  meta: {
    inputType: 'textArea',
    validations: {
      minLength: [
        { conditions: { fit: ['*'] } as any, value: 1 },
      ],
      maxLength: [
        { conditions: { fit: ['*'] } as any, value: 500 },
      ],
      noOfWords: [
        { conditions: { fit: ['*'] } as any, value: 3 },
      ],
    },
    minRows: [{ conditions: {}, value: 1 }],
    maxRows: [{ conditions: {}, value: 10 }],
    autoExtend: [{ conditions: {}, value: true }],
  },

}

export const sampleNumberData: IMetaUnit<IMetaNumberUnit> = {
  name: 'passPercentage',
  defaultValue: [],
  format: 'number',
  info: [
    { conditions: {}, value: '' },
  ],
  displayName: [
    { conditions: { fit: ['*'] }, value: '' },
  ],
  isMandatory: [
    { fit: ['*'] } as any,
  ],
  isDisabled: [],
  canCascade: false,
  canShow: [
    { fit: ['*'] } as any,
  ],
  meta: {
    inputType: 'number',
    validations: {
      minValue: [
        { conditions: { fit: ['*'] } as any, value: 1 },
      ],
      maxValue: [
        { conditions: { fit: ['*'] } as any, value: 60 },
      ],
    },
  },

} as any

export const sampleData1: IMetaUnit<IMetaTextUnit> = {
  name: 'name',
  defaultValue: [],
  format: 'string',
  info: [
    { conditions: {}, value: '' },
  ],
  displayName: [
    { conditions: { fit: ['*'] }, value: '' },
  ],
  isMandatory: [
    { fit: ['*'] } as any,
  ],
  isDisabled: [],
  canCascade: false,
  canShow: [
    { fit: ['*'] } as any,
  ],
  meta: {
    inputType: 'text',
    validations: {
      minLength: [
        { conditions: { fit: ['*'] } as any, value: 1 },
      ],
      maxLength: [
        { conditions: { fit: ['*'] } as any, value: 500 },
      ],
      noOfWords: [
        { conditions: { fit: ['*'] } as any, value: 3 },
      ],
    },
  },

} as any
