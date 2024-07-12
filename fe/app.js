document.getElementById('mergeForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const files = document.getElementById('files').files;
    const orderInput = document.getElementById('order').value;

    if (files.length === 0) {
        alert("Please select at least one PDF file.");
        return;
    }

    if (orderInput.trim() === "") {
        alert("Please enter the order of the files.");
        return;
    }

    const orderArray = orderInput.split(',').map(entry => {
        const [fileName, order] = entry.split(':');
        return { fileName: fileName.trim(), order: parseInt(order.trim()) };
    });

    const formData = new FormData();
    for (const file of files) {
        formData.append('file', file);
    }
    formData.append('body', new Blob([JSON.stringify(orderArray)], { type: 'application/json' }));

    try {
        const response = await fetch('/ordered-merge', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const blob = await response.blob();
        const downloadLink = document.getElementById('downloadLink');
        const url = URL.createObjectURL(blob);

        downloadLink.href = url;
        downloadLink.download = 'MergedPDF.pdf';
        downloadLink.style.display = 'block';
        downloadLink.textContent = 'Download Merged PDF';

    } catch (error) {
        document.getElementById('info').textContent = 'Error: ' + error.message;
    }
});