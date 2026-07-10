export const apiBaseUrl = 'http://localhost:8080/api';
export const fetchApi = async (endpoint: string, options?: RequestInit) => {
    const response = await fetch(${apiBaseUrl}, options);
    if (!response.ok) throw new Error('API Error');
    return response.json();
};
