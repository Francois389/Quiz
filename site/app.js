(() => {
  const titreInput = document.getElementById('titre');
  const descriptionInput = document.getElementById('description');
  const questionsContainer = document.getElementById('questions-container');
  const questionTemplate = document.getElementById('question-template');
  const wrongAnswerTemplate = document.getElementById('wrong-answer-template');

  const addQuestionBtn = document.getElementById('add-question');
  const exportBtn = document.getElementById('export-json');
  const previewBtn = document.getElementById('preview-json');
  const closePreviewBtn = document.getElementById('close-preview');
  const previewContainer = document.getElementById('preview-container');
  const jsonPreview = document.getElementById('json-preview');

  const importBtn = document.getElementById('import-json');
  const fileInput = document.getElementById('file-input');
  const downloadApkBtn = document.getElementById('download-apk');

  function showToast(message, isError = false) {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.classList.remove('hidden', 'error');
    if (isError) toast.classList.add('error');
    clearTimeout(showToast._t);
    showToast._t = setTimeout(() => toast.classList.add('hidden'), 2500);
  }

  function renumberQuestions() {
    const cards = questionsContainer.querySelectorAll('.question-card');
    cards.forEach((card, index) => {
      card.querySelector('.question-number').textContent = `Question ${index + 1}`;
    });
  }

  const WRONG_ANSWERS_COUNT = 3;

  function addWrongAnswerRow(wrongAnswersDiv, value = '') {
    const node = wrongAnswerTemplate.content.cloneNode(true);
    const input = node.querySelector('.q-wrong');
    input.value = value;
    wrongAnswersDiv.appendChild(node);
  }

  function addQuestion(data = null) {
    const node = questionTemplate.content.cloneNode(true);
    const card = node.querySelector('.question-card');
    const wrongAnswersDiv = card.querySelector('.wrong-answers');

    card.querySelector('.btn-remove').addEventListener('click', () => {
      card.remove();
      renumberQuestions();
    });

    if (data) {
      card.querySelector('.q-text').value = data.question || '';
      card.querySelector('.q-good').value = data.bonneReponse || '';
    }

    const wrongs = (data && Array.isArray(data.mauvaiseReponse)) ? data.mauvaiseReponse : [];
    for (let i = 0; i < WRONG_ANSWERS_COUNT; i++) {
      addWrongAnswerRow(wrongAnswersDiv, wrongs[i] || '');
    }

    questionsContainer.appendChild(node);
    renumberQuestions();
  }

  function buildQuizObject() {
    const titre = titreInput.value.trim();
    const description = descriptionInput.value.trim();

    const questions = [];
    const cards = questionsContainer.querySelectorAll('.question-card');

    cards.forEach((card) => {
      const question = card.querySelector('.q-text').value.trim();
      const bonneReponse = card.querySelector('.q-good').value.trim();
      const mauvaiseReponse = Array.from(card.querySelectorAll('.q-wrong'))
      .map(input => input.value.trim());

      if (question || bonneReponse || mauvaiseReponse.some(v => v.length > 0)) {
        questions.push({ question, bonneReponse, mauvaiseReponse });
      }
    });

    return { titre, description, questions };
  }

  function validateQuiz(quiz) {
    const errors = [];
    if (!quiz.titre) errors.push('Le titre est requis.');
    if (quiz.questions.length === 0) errors.push('Ajoute au moins une question.');

    quiz.questions.forEach((q, i) => {
      const n = i + 1;
      if (!q.question) errors.push(`Question ${n} : l'intitulé est vide.`);
      if (!q.bonneReponse) errors.push(`Question ${n} : la bonne réponse est vide.`);
      const nonVides = q.mauvaiseReponse.filter(v => v.length > 0);
      if (nonVides.length !== 3) errors.push(`Question ${n} : il faut exactement 3 mauvaises réponses.`);
    });

    return errors;
  }

  addQuestionBtn.addEventListener('click', () => addQuestion());

  previewBtn.addEventListener('click', () => {
    const quiz = buildQuizObject();
    jsonPreview.textContent = JSON.stringify(quiz, null, 2);
    previewContainer.classList.remove('hidden');
    previewContainer.scrollIntoView({ behavior: 'smooth' });
  });

  closePreviewBtn.addEventListener('click', () => {
    previewContainer.classList.add('hidden');
  });

  exportBtn.addEventListener('click', () => {
    const quiz = buildQuizObject();
    const errors = validateQuiz(quiz);

    if (errors.length > 0) {
      showToast(errors[0], true);
      return;
    }

    quiz.questions.forEach(q => {
      q.mauvaiseReponse = q.mauvaiseReponse.filter(v => v.length > 0);
    });

    const blob = new Blob([JSON.stringify(quiz, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    const filename = quiz.titre
    ? quiz.titre.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '')
    : 'quiz';
    a.href = url;
    a.download = `${filename || 'quiz'}.json`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
    showToast('Fichier JSON exporté !');
  });

  importBtn.addEventListener('click', () => fileInput.click());

  fileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const data = JSON.parse(event.target.result);

        titreInput.value = data.titre || '';
        descriptionInput.value = data.description || '';

        questionsContainer.innerHTML = '';
        const questions = Array.isArray(data.questions) ? data.questions : [];
        questions.forEach(q => addQuestion(q));

        if (questions.length === 0) addQuestion();

        showToast('Questionnaire importé.');
      } catch (err) {
        showToast('Fichier JSON invalide.', true);
      }
    };
    reader.readAsText(file);
    fileInput.value = '';
  });

  function parseGithubRepoUrl(url) {
    try {
      const u = new URL(url);
      if (u.hostname !== 'github.com') return null;
      const parts = u.pathname.replace(/^\/|\/$/g, '').split('/');
      if (parts.length < 2) return null;
      const [owner, repo] = parts;
      return { owner, repo: repo.replace(/\.git$/, '') };
    } catch {
      return null;
    }
  }

  async function handleApkDownloadClick() {
    console.log("Click!")
    const repoUrl = window.APP_CONFIG && window.APP_CONFIG.repoUrl;
    if (!repoUrl) {
      showToast("Aucun dépôt GitHub configuré.", true);
      return;
    }

    const parsed = parseGithubRepoUrl(repoUrl);
    if (!parsed) {
      showToast("URL de dépôt GitHub invalide.", true);
      return;
    }

    const originalText = downloadApkBtn.textContent;
    downloadApkBtn.disabled = true;
    downloadApkBtn.textContent = 'Recherche de la dernière version...';

    try {
      const res = await fetch(`https://api.github.com/repos/${parsed.owner}/${parsed.repo}/releases/latest`);
      if (!res.ok) throw new Error(`GitHub API: ${res.status}`);
      const release = await res.json();

      const apkAsset = (release.assets || []).find(a => a.name.toLowerCase().endsWith('.apk'));
      if (!apkAsset) {
        showToast("Aucun APK trouvé dans la dernière release.", true);
        return;
      }

      window.location.href = apkAsset.browser_download_url;
    } catch (err) {
      showToast("Impossible de récupérer la dernière release.", true);
    } finally {
      downloadApkBtn.disabled = false;
      downloadApkBtn.textContent = originalText;
    }
  }

  downloadApkBtn.addEventListener('click', handleApkDownloadClick);

  // Démarrage avec une première question vide
  addQuestion();
})();
