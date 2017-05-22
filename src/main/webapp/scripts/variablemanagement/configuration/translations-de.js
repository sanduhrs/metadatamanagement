'use strict';

angular.module('metadatamanagementApp').config(
  function($translateProvider) {
    var translations = {
      //jscs:disable
      'variable-management': {
        'log-messages': {
          'variable': {
            'unable-to-read-file': 'Die Datei {{file}} in {{dataSet}} kann nicht geöffnet werden!',
            'json-parse-error': 'Die Datei {{file}} in {{dataSet}} kann nicht geparst werden!',
            'saved': 'Variable mit FDZ-ID {{ id }} erfolgreich gespeichert!',
            'not-saved': 'Variable mit FDZ-ID {{ id }} wurde nicht gespeichert:',
            'missing-id': 'Die {{ index }}. Variable enthält keine FDZ-ID und wurde nicht gespeichert!',
            'upload-terminated': 'Upload von {{ total }} Variablen mit {{warnings}} Warnungen und {{ errors }} Fehlern beendet!',
            'unable-to-delete': 'Die Variablen konnten nicht gelöscht werden!',
            'cancelled': 'Upload von Variablen Abgebrochen!',
            'missing-json-file': 'Keine JSON Datei zu Variable {{name}} in Datensatz {{dataSet}} gefunden!',
            'missing-excel-file': 'Keine Excel Datei in Datensatz {{dataSet}} gefunden!',
            'missing-name': 'Die {{variableIndex}}. Variable in Datensatz {{dataSet}} hat keinen Namen!',
            'generation-details-rule-success-copy-to-clipboard': 'Die Generierungsregel wurde erfolgreich in die Zwischenablage kopiert.',
            'filter-details-success-copy-to-clipboard': 'Der Filterausdruck wurde erfolgreich in die Zwischenablage kopiert.',
            'duplicate-name': 'Der Name ({{ name }}) der {{ index }}. Variable aus Datensatz {{ dataSetNumber }} wurde bereits verwendet.',
            'distribution': {
              'histogram': {
                'incomplete-histogram-information': 'Die Variable {{ variableName }} in Datensatz {{ dataSetName }} ist verhältnisskaliert, hat aber nicht alle Informationen für die Darstellung eines Histogramms.'
              }
            }
          }
        },
        'detail': {
          'title': '{{ label }} ({{ variableId }})',
          'variable': 'Variable',
          'variables': 'Variablen',
          'variable-informations': 'Informationen',
          'variables-in-panel': 'Panelvariablen',
          'related-objects': 'Zugehörige Objekte',
          'frequencies': 'Häufigkeiten',
          'generation-details': 'Generierungsdetails',
          'name': 'Name',
          'data-type': 'Datentyp',
          'scale-level': 'Skalenniveau',
          'question-text': 'Fragentext',
          'access-ways': 'Zugangswege',
          'annotations': 'Anmerkungen',
          'filter-description': 'Beschreibung des Filters',
          'filter-details-language': 'Filterausdruckssprache',
          'filter-details-expression': 'Filterausdruck',
          'input-filter': 'Eingangsfilter',
          'generation-details-description': 'Generierungsbeschreibung',
          'generation-details-expression-language': 'Generierungsausdruckssprache',
          'generation-details-rule': 'Generierungsregel',
          'copy-complete-input-filter-tooltip': 'Klicken, um den gesamten Inhalt des Eingangsfilters in die Zwischenablage zu kopieren',
          'show-complete-input-filter-tooltip': {
            'true': 'Klicken, um den gesamten Inhalt des Eingangsfilters zu zeigen',
            'false': 'Klicken, um den Inhaltsbereich des Eingangsfilters zu minimieren'
          },
          'copy-complete-rule-tooltip': 'Klicken, um den gesamten Inhalt der Generierungsregel zu kopieren',
          'show-complete-rule-tooltip': {
            'true': 'Klicken, um den gesamten Inhalt der Generierungsregel zu zeigen',
            'false': 'Klicken, um den Inhaltsbereich der Generierungsregel zu minimieren'
          },
          'label': 'Label',
          'not-found': 'Die id {{id}} referenziert auf eine unbekannte Variable.',
          'not-found-references': 'Die id {{id}} hat keine Referenzen auf Variablen.',
          'same-in-panel': 'Ähnliche Variablen',
          'show-complete-distribution-tooltip': {
            'true': 'Klicken, um den gesamten Inhalt der Häufigkeitstabelle zu zeigen',
            'false': 'Klicken, um den Inhaltsbereich der Häufigkeitstabelle zu minimieren'
          },
          'statistics': {
            'graphics': 'Abbildung Häufigkeiten/Verteilung (gültige Werte)',
            'graphic-is-loading': 'wird geladen...',
            'graphic-is-not-available': 'Keine Grafische Darstellung Der Statistiken',
            'statistics': 'Deskriptive Maßzahlen',
            'all': 'Alle',
            'page': 'Seite',
            'rowsPerPage': 'Zeilen Pro Seite',
            'of': 'von',
            'value': 'Wert',
            'label': 'Label',
            'frequency': 'Häufigkeit',
            'valid-percent': 'Prozent (gültig)',
            'percent': 'Prozent',
            'firstQuartile': 'Unteres Quartil',
            'highWhisker': 'Oberer Whisker',
            'lowWhisker': 'Unterer Whisker',
            'maximum': 'Maximum',
            'median': 'Median',
            'minimum': 'Minimum',
            'thirdQuartile': 'Oberes Quartil',
            'validResponses': 'Anzahl unterschiedlicher Beobachtungen',
            'total-absolute-frequency': 'Anzahl unterschiedlicher Beobachtungen',
            'totalValidAbsoluteFrequency': 'Totale Valide Absolute Häufigkeit',
            'totalValidRelativeFrequency': 'Totale Valide Relative Häufigkeit',
            'mean-value': 'Arithmetisches Mittel',
            'skewness': 'Schiefe',
            'kurtosis': 'Wölbung',
            'standardDeviation': 'Standardabweichung',
            'mode': 'Modus',
            'deviance': 'Devianz',
            'mean-deviation': 'Durchschnittliche Abweichung'
          },
          'no-related-variables': 'Keine zugehörige Variablen.',
          'related-variables': 'Zugehörige Variablen',
          'central-tendency': 'Zentrale Tendenz',
          'dispersion': 'Streuung',
          'distribution': 'Verteilung',
          'not-released-toast': 'Die Variable "{{ id }}" wurde noch nicht für alle Benutzer freigegeben!',
          'tooltips': {
            'surveys': {
              'one': 'Klicken, um die Erhebung anzuzeigen, aus der diese Variable resultierte',
              'many': 'Klicken, um alle Erhebungen anzuzeigen, aus denen diese Variable resultierte'
            },
            'data-sets': {
              'one': 'Klicken, um den Datensatz anzuzeigen, in dem diese Variable enthalten ist'
            },
            'publications': {
              'one': 'Klicken, um die Publikation zu dieser Variable anzuzeigen',
              'many': 'Klicken, um alle Publikationen zu dieser Variable anzuzeigen'
            },
            'questions': {
              'one': 'Klicken, um die Frage anzuzeigen, aus der diese Variable resultierte',
              'many': 'Klicken, um die Fragen anzuzeigen, aus denen diese Variable resultierte'
            },
            'variables': {
              'same-in-panel': 'Klicken, um alle Panelvariablen zu dieser Variable anzuzeigen'
            },
            'studies': {
              'one': 'Klicken, um die Studie anzuzeigen, aus der diese Variable resultierte'
            }
          }
        },
        'labels': {
          'part-of-data-set': 'Enthalten in Datensatz:',
          'surveyed-in': 'Erhoben in:'
        },
        'error': {
          'distribution': {
            'valid-responses': {
              'unique-value': 'Der Wert muss innerhalb der gültigen Antworten eindeutig sein.'
            },
            'total-absolute-frequency': {
              'not-null': 'Die totale, absolute Häufigkeit darf nicht leer sein!'
            },
            'total-valid-absolute-frequency': {
              'not-null': 'Die totale, valide, absolute Häufigkeit darf nicht leer sein!'
            },
            'total-valid-relative-frequency': {
              'not-null': 'Die totale, valide, relative Häufigkeit darf nicht leer sein!'
            },
            'missings': {
              'uniqueCode': 'Der Code muss innerhalb der Missings eindeutig sein.'
            }
          },
          'filter-details': {
            'expression': {
              'not-empty': 'Der Filterausdruck darf nicht leer sein!',
              'size': 'Die Maximallänge des Filterausdrucks ist 128 Zeichen.'
            },
            'description': {
              'i18n-string-size': 'Die Maximallänge der Filterbeschreibung ist 2048 Zeichen.'
            },
            'expression-language': {
              'not-empty': 'Die Filterausdruckssprache darf nicht leer sein!',
              'valid-filter-expression-language': 'Die angegebene Filterausdruckssprache ist nicht gültig.Es sind nur SpEL und Stata erlaubt.'
            }
          },
          'generation-details': {
            'not-empty-generation-details-description-or-rule': 'Es muss bei der Regelgenerierung mind. die Regel oder die Beschreibung gesetzt sein.',
            'rule-expression-language-and-rule-filled-or-empty': 'Die Generierungsregel und die dazugehörige Sprachen müssen beide gefüllt sein oder leer sein.',
            'description': {
              'i18n-string-size': 'Die Maximallänge der Beschreibung der Regelgenerierung der Variable ist 2048 Zeichen.'
            },
            'rule': {
              'size': 'Die Maximallänge der Generierungsregel ist 1048576 Zeichen.'
            },
            'rule-expression-language': {
              'valid-rule-expression-language': 'Die Sprache der Generierungsregel ist nicht gültig.Nur R und Stata sind erlaubt.'
            }
          },
          'missing': {
            'code': {
              'not-null': 'Der Code eines Missings darf nicht leer sein!'
            },
            'label': {
              'i18n-string-size': 'Die Maximallänge des Labels eines Missings ist 128 Zeichen.'
            },
            'absolute-frequency': {
              'not-null': 'Die absolute Häufigkeit eines Missings darf nicht leer sein.'
            },
            'relative-frequency': {
              'not-null': 'Die relative Häufigkeit eines Missings darf nicht leer sein.'
            }
          },
          'valid-response': {
            'label': {
              'i18n-string-size': 'Die Maximallänge des Labels einer gültigen Antwort ist 2048 Zeichen.'
            },
            'absolute-frequency': {
              'not-null': 'Die absolute Häufigkeit einer gültigen Antwort darf nicht leer sein.'
            },
            'relative-frequency': {
              'not-null': 'Die relative Häufigkeit einer gültigen Antwort darf nicht leer sein.'
            },
            'value': {
              'size': 'Die Maximallänge eines Wertes ist 256 Zeichen.',
              'not-null': 'Der Wert einer gültigen Antwort darf nicht leer sein!'
            },
            'valid-relative-frequency': {
              'not-null': 'Die gültige, relative Häufigkeit einer gültigen Antwort darf nicht leer sein.'
            }
          },
          'statistics': {
            'minimum': {
              'size': 'Die Maximallänge des Minimum ist 32 Zeichen.'
            },
            'maximum': {
              'size': 'Die Maximallänge des Maximum ist 32 Zeichen.'
            },
            'median': {
              'size': 'Die Maximallänge des Medians ist 32 Zeichen.'
            },
            'first-quartile': {
              'size': 'Die Maximallänge des ersten Quartiles ist 32 Zeichen.'
            },
            'third-quartile': {
              'size': 'Die Maximallänge des dritten Quartiles ist 32 Zeichen.'
            }
          },
          'variable': {
            'valid-variable-name': 'Die FDZ-ID der Variable entspricht nicht dem Muster: "var-" + {FDZID} + "-ds" + {DataSetNmber} + "-" + {Variablenname}+ "$".',
            'unique-variable-name-in-data-set': 'Der Name der Variable ist innerhalb des Datensatzes schon vergeben.',
            'data-set-number-not-null': 'Die Nummer des Datensatzes der Variable darf nicht leer sein!',
            'data-set-id-not-empty': 'Die FDZ-ID des Datensatzes der Variable darf nicht leer sein!',
            'data-set-index-not-null': 'Der Index des Datensatzes der Variable darf nicht leer sein!',
            'survey-numbers-not-empty': 'Die Liste der Erhebungsnummern einer Variable benötigt mindest ein Element und darf nicht leer sein!',
            'related-question-number-size': 'Die Maximallänge der Nummer einer Frage ist 32 Zeichen.',
            'related-question-number-not-empty': 'Eine verbundene Frage hat keine Nummer',
            'related-question-instrument-number-not-empty': 'Ein Instrument hat keine Nummer',
            'valid-identifier': 'Die Panel-Kennung der Variable entspricht nicht dem Muster: DataAcquisitionProjectId + "-" + "ds" + "dataSetNumber" + "-" + string',
            'panel-identifier-size': 'Die Maximallänge der Panel-Kennung der Variable ist 128 Zeichen.',
            'panel-identifier-pattern': 'Für die Panel-Kennung der Variable dürfen nur alphanumerische Zeichen, deutsche Umlaute, ß und das Minus verwendet werden.',
            'only-ordinal-scale-level-for-date-data-type': 'Das Skalenniveau einer Datumsvariable muss ordinal sein.',
            'valid-response-value-must-be-a-number-on-numeric-data-type': 'Wenn der Datentyp einer Variable numerisch ist, müssen die Werte von gültigen Antworten numerisch sein.',
            'statistics-minimum-must-be-a-number-on-numeric-data-type': 'Wenn der Datentyp einer Variable numerisch ist, müssen das Minimum von Statistiken numerisch sein.',
            'statistics-maximum-must-be-a-number-on-numeric-data-type': 'Wenn der Datentyp einer Variable numerisch ist, muss das Maximum von Statistiken numerisch sein.',
            'statistics-median-must-be-a-number-on-numeric-data-type': 'Wenn der Datentyp einer Variable numerisch ist, muss der Median von Statistiken numerisch sein.',
            'statistics-first-quartile-must-be-a-number-on-numeric-data-type': 'Wenn der Datentyp einer Variable numerisch ist, muss das erste Quartile von Statistiken numerisch sein.',
            'statistics-third-quartile-must-be-a-number-on-numeric-data-type': 'Wenn der Datentyp einer Variable numerisch ist, muss das dritte Quartile von Statistiken numerisch sein.',
            'valid-response-value-must-be-an-iso-date-on-date-data-type': 'Wenn der Datentyp einer Variable ein Datum ist, müssen die Werte von gültigen Antworten dem Format yyyy-MM-dd entsprechen.',
            'statistics-minimum-must-be-an-iso-date-on-date-data-type': 'Wenn der Datentyp einer Variable ein Datum ist, muss das Minimum von den Statistiken dem Format yyyy-MM-dd entsprechen.',
            'statistics-maximum-must-be-an-iso-date-on-date-data-type': 'Wenn der Datentyp einer Variable ein Datum ist, muss das Maximum von den Statistiken dem Format yyyy-MM-dd entsprechen.',
            'statistics-median-must-be-an-iso-date-on-date-data-type': 'Wenn der Datentyp einer Variable ein Datum ist, muss der Median von den Statistiken dem Format yyyy-MM-dd entsprechen.',
            'statistics-first-quartile-must-be-an-iso-date-on-date-data-type': 'Wenn der Datentyp einer Variable ein Datum ist, muss das erste Quartile von den Statistiken dem Format yyyy-MM-dd entsprechen.',
            'statistics-third-quartile-must-be-an-iso-date-on-date-data-type': 'Wenn der Datentyp einer Variable ein Datum ist, muss das dritte Quartile von den Statistiken dem Format yyyy-MM-dd entsprechen.',
            'id': {
              'not-empty': 'Die FDZ - ID der Variable darf nicht leer sein!',
              'size': 'Die Maximallänge der FDZ - ID ist 128 Zeichen.',
              'pattern': 'Es dürfen für die FDZ - ID nur alphanumerische Zeichen, deutsche Umlaute, ß, Minus, Ausrufezeichen und der Unterstrich verwendet werden.'
            },
            'data-type': {
              'not-null': 'Der Datentyp der Variable darf nicht leer sein!',
              'valid-data-type': 'Die Werte für Datentyp sind nicht gültig. Erlaubt in deutsch: string, numerisch, datum. Erlaubt in englisch: string, numeric, date.'
            },
            'scaleLevel': {
              'valid-scale-level': 'Die Werte für das Skalenniveau sind nicht gültig. Erlaubt in deutsch: nominal, ordinal, intervall, verhältnis.Erlaubt in englisch: nominal, ordinal, interval, ratio.',
              'not-null': 'Das Skalenniveau einer Variable darf nicht leer sein!'
            },
            'name': {
              'not-empty': 'Der Name der Variable darf nicht leer sein!',
              'size': 'Die Maximallänge des Namens ist 32 Zeichen.',
              'pattern': 'Für den Namen dürfen nur alphanumerische Zeichen und das Minus verwendet werden.'
            },
            'label': {
              'not-null': 'Das Label der Variable darf nicht leer sein!',
              'i18n-string-size': 'Die die Maximallänge für die Label ist 128 Zeichen.',
              'i18n-string-not-empty': 'Mindestens ein deutsches oder ein englisches Label muss angegeben werden!'
            },
            'annotations': {
              'i18n-string-size': 'Die Maximallänge der Anmerkungen ist 2048 Zeichen.'
            },
            'access-ways': {
              'not-empty': 'Die Liste der Zugangswege einer Variable benötigt mindest ein Element und darf nicht leer sein!',
              'valid-access-ways': 'Die Liste der Zugangswege enthält ungültige Werte. Erlaubt sind nur: download-cuf, download-suf, remote-desktop-suf, onsite-suf, not-accessible.'
            },
            'survey': {
              'ids': {
                'not-empty': 'Die Variable muss mindestens einer Erhebung zugewiesen sein!'
              }
            },
            'related-question-strings': {
              'i18n-string-size': 'Die Maximallänge der zugehörigen Frage-Strings ist 2048 Zeichen.'
            },
            'data-acquisition-project': {
              'id': {
                'not-empty': 'Die FDZ - ID des Projektes darf bei der Variable nicht leer sein!'
              }
            }
          },
          'post-validation': {
            'variable-has-invalid-survey-id': 'Die Variable {{id}} referenziert auf eine unbekannte Erhebung ({{toBereferenzedId}}).',
            'variable-id-is-not-valid-in-related-variables': 'Die Variable {{id}} referenziert auf eine unbekannte verwandte Variable ({{toBereferenzedId}}).',
            'variable-has-invalid-data-set-id': 'Die Variable {{id}} referenziert auf einen unbekannten Datensatz ({{toBereferenzedId}}).',
            'variable-has-invalid-question-id': 'Die Variable {{id}} referenziert auf einen unbekannte Frage ({{toBereferenzedId}}).',
            'variable-survey-ids-are-not-consistent-with-data-set': 'Die Variable {{id}} referenziert auf andere Erhebungen als ihr Datensatz {{toBereferenzedId}}.'
          }
        }
      }
      //jscs:enable
    };
    $translateProvider.translations('de', translations);
  });
